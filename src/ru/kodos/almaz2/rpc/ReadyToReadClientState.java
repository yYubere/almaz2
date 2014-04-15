package ru.kodos.almaz2.rpc;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import ru.kodos.almaz2.rpc.commands.RpcMessageClass.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;


/**
 * Created by Андрей on 08.04.2014.
 */
public class ReadyToReadClientState implements ClientState {
    RPCClient rpcClient;
    RpcProtocol.Builder messageOut;

    public ReadyToReadClientState(RPCClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    public void writeMessage(SelectionKey key) {

        if (messageOut != null) {
            System.out.println("Send Message");
            try {
                SocketChannel channel = (SocketChannel) key.channel();
                ByteBuffer buffer = (ByteBuffer) key.attachment();
                Message buildMessage = messageOut.build();
                buildMessage.writeTo(CodedOutputStream.newInstance(buffer.array()));
                buffer.limit(buildMessage.getSerializedSize());
                buffer.rewind();
                while (buffer.hasRemaining()) {
                    channel.write(buffer);
                }
                buffer.clear();
                messageOut = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void readMessage(SelectionKey key) throws IOException {
//        try {
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        ByteBuffer buffer = (ByteBuffer) key.attachment();
        SocketChannel channel = (SocketChannel) key.channel();
        if (channel.read(buffer) != -1) {
            buffer.flip();
            ByteBufferBackedInputStream inputStream = new ByteBufferBackedInputStream(buffer);
            while (true) {
                RpcProtocol messageIn = RpcProtocol.parseDelimitedFrom(inputStream);
                if (messageIn == null) break;
                processMessage(messageIn);
            }
            buffer.compact();
        } else {
            throw InvalidClientStateException.negativeSize();
        }
    }

    private void processMessage(RpcProtocol messageIn) {
        System.out.println(messageIn.toString());
        if (messageIn.hasReply() && messageIn.getReply().hasAckRequest()) {
            processAckRequest(messageIn);
        }
    }

    private void processAckRequest(RpcProtocol messageIn) {
        messageOut = RpcProtocol.newBuilder();
        RpcProtocol.RequestBase.Builder request = RpcProtocol.RequestBase.newBuilder();
        RpcProtocol.AckAnswer.Builder ackAnswer = RpcProtocol.AckAnswer.newBuilder();
        ackAnswer.setAckId(messageIn.getReply().getAckRequest().getAckId());
        request.setAckAnswer(ackAnswer);
        messageOut.setRequest(request);
    }
}
