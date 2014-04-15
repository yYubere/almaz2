package ru.kodos.almaz2.rpc;
import ru.kodos.almaz2.rpc.commands.RpcMessageClass.*;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Message;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by Андрей on 11.04.2014.
 */
public class NotLoginnedClientState implements ClientState {
    RPCClient rpcClient;

    public NotLoginnedClientState(RPCClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    public void writeMessage(SelectionKey key) {
        System.out.println("Send LoginRequest");
        // Формируем команду авторизации.
        // Выдаем команду серверу.
        RpcProtocol.Builder out_message = RpcProtocol.newBuilder();
        RpcProtocol.RequestBase.Builder request = RpcProtocol.RequestBase.newBuilder();
        RpcProtocol.UserLoginRequest.Builder userLoginRequest = RpcProtocol.UserLoginRequest.newBuilder();
        userLoginRequest.setLogin("user");
        userLoginRequest.setPassword("");
        request.setUserLogin(userLoginRequest);
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
        try {
            ByteBuffer buffer = (ByteBuffer) key.attachment();
            SocketChannel channel = (SocketChannel) key.channel();
            if (channel.read(buffer) != -1) {
                buffer.flip();
                ByteBufferBackedInputStream inputStream = new ByteBufferBackedInputStream(buffer);
                RpcProtocol message = RpcProtocol.parseDelimitedFrom(inputStream);
                buffer.compact();

                if (message.hasReply()) {
                    if (message.getReply().hasUserLogin()) {
                        int errorCode = message.getReply().getUserLogin().getErrorCode();
                        switch (errorCode) {
                            case 0:
                                System.out.println("User is login.");
                                rpcClient.setState(rpcClient.getReadyToReadClientState());
                                rpcClient.stateChanged();
                                break;
                            default:
                                System.out.println("server: ErrorCode="+message.getReply().getAuth().getErrorCode());
                                rpcClient.setState(rpcClient.getNotAuthorizedClientState());
                        }
                    }
                }
            } else {
                InvalidClientStateException.negativeSize();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
