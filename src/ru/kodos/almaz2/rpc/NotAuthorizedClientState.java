package ru.kodos.almaz2.rpc;
import com.google.protobuf.CodedInputStream;
import ru.kodos.almaz2.rpc.commands.RpcMessageClass.*;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Message;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created with IntelliJ IDEA.
 * User: maslov
 * Date: 24.03.14
 * Time: 13:52
 * To change this template use File | Settings | File Templates.
 */
public class NotAuthorizedClientState implements ClientState {
    RPCClient rpcClient;

    public NotAuthorizedClientState(RPCClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    public void writeMessage(SelectionKey key) {
        System.out.println("Send AuthRequest");
        // Формируем команду авторизации.
        // Выдаем команду серверу.
        RpcProtocol.Builder out_message = RpcProtocol.newBuilder();
        RpcProtocol.RequestBase.Builder request = RpcProtocol.RequestBase.newBuilder();
        RpcProtocol.AuthRequest.Builder authRequest = RpcProtocol.AuthRequest.newBuilder();
        authRequest.setClientId(1);
        authRequest.setConnectionId(1);
        authRequest.setSession("11111");
        authRequest.setAuthKey(rpcClient.signer.sign(authRequest.getSession()));
        request.setAuth(authRequest);
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
                    if (message.getReply().hasAuth()) {
                        int errorCode = message.getReply().getAuth().getErrorCode();
                        switch (errorCode) {
                            case 0:
                                if (rpcClient.signer.verify(message.getReply().getAuth().getAuthKey(), "11111")) {
                                    System.out.println("Client is authorized (signature is verified).");
                                    rpcClient.setState(rpcClient.getNotLoginnedClientState());
                                    rpcClient.stateChanged();
                                }
                                else {
                                    System.out.println("Client is NOT authorized (signature is not verified).");
                                    rpcClient.setState(rpcClient.getNotAuthorizedClientState());
                                }
                                break;
                            case 1004:
                                System.out.println("Client is not registered.");
                                rpcClient.setState(rpcClient.getNotRegisteredClientState());
                                break;
                            case 1008:
                                System.out.println("server: Signature is not verified.");
                                rpcClient.setState(rpcClient.getNotAuthorizedClientState());
                                break;
                            default:
                                System.out.println("server: ErrorCode="+message.getReply().getAuth().getErrorCode());
                                rpcClient.setState(rpcClient.getNotAuthorizedClientState());
                        }

                        if (errorCode != 0) {
                            InvalidClientStateException exception = InvalidClientStateException.finishedState();
                            exception.setUnfinishedState(rpcClient.getNotAuthorizedClientState());
                            throw exception;
                        }
                    }
                } else {
                    throw InvalidClientStateException.negativeSize();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
