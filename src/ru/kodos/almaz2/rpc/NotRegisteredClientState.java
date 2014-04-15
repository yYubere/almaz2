package ru.kodos.almaz2.rpc;

import com.google.protobuf.CodedInputStream;
import ru.kodos.almaz2.rpc.commands.RpcMessageClass.*;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Message;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created with IntelliJ IDEA.
 * User: maslov
 * Date: 24.03.14
 * Time: 13:43
 * To change this template use File | Settings | File Templates.
 */
public class NotRegisteredClientState implements ClientState {
    RPCClient rpcClient;

    public NotRegisteredClientState(RPCClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    public void writeMessage(SelectionKey key) {
        System.out.println("Send PrimaryAuthRequest");
        RpcProtocol.Builder out_message = RpcProtocol.newBuilder();
        RpcProtocol.RequestBase.Builder request = RpcProtocol.RequestBase.newBuilder();
        RpcProtocol.PrimaryAuthRequest.Builder primaryAuthRequest = RpcProtocol.PrimaryAuthRequest.newBuilder();
        primaryAuthRequest.setClientId(1);
        primaryAuthRequest.setPrimaryKey("primarykey");
        request.setPrimaryAuth(primaryAuthRequest);
        out_message.setRequest(request);

        try {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = (ByteBuffer) key.attachment();
            Message buildMessage = out_message.build();
            buildMessage.writeTo(CodedOutputStream.newInstance(buffer.array()));
            buffer.limit(buildMessage.getSerializedSize());
            buffer.rewind();
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
            buffer.compact();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void readMessage(SelectionKey key)  throws IOException {
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        SocketChannel channel = (SocketChannel) key.channel();
        if (channel.read(buffer) != -1) {
            buffer.flip();
            ByteBufferBackedInputStream inputStream = new ByteBufferBackedInputStream(buffer);
            RpcProtocol message = RpcProtocol.parseDelimitedFrom(inputStream);
            buffer.compact();

            if (message.hasReply()) {
                if (message.getReply().hasPrimaryAuth()) {
                    int errorCode = message.getReply().getPrimaryAuth().getErrorCode();
                    switch (errorCode) {
                        case 0:
                            writeKey("public_key.pem", message.getReply().getPrimaryAuth().getPublicKey());
                            writeKey("private_key.pem", message.getReply().getPrimaryAuth().getPrivateKey());
                            rpcClient.setState(rpcClient.getNotAuthorizedClientState());
                            rpcClient.stateChanged();
                            throw InvalidClientStateException.finishedState().setUnfinishedState(
                                    rpcClient.getNotRegisteredClientState());
                        default:
                            rpcClient.setState(rpcClient.getNotAuthorizedClientState());
                            throw InvalidClientStateException.invalidRegistration();
                    }
                }
            }
        } else {
            throw InvalidClientStateException.negativeSize();
        }
    }

    private void writeKey(String fileName, String key) {
        try {
            PrintWriter publicKeyFile = new PrintWriter(new FileOutputStream(fileName));
            publicKeyFile.print(key);
            publicKeyFile.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


}
