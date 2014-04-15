package ru.kodos.almaz2.rpc;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Message;
import ru.kodos.almaz2.rpc.commands.RpcMessageClass.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Observable;
import java.util.Set;

/**
 * Created by Андрей on 06.04.2014.
 */
public class RPCClient extends Observable implements Runnable {
    ClientState notAuthorizedClientState;
    ClientState notLoginnedClientState;
    ClientState notRegisteredClientState;
    ClientState readyToReadClientState;
    ClientState state;
    PSSSigner signer;

    public RPCClient() {
        notAuthorizedClientState = new NotAuthorizedClientState(this);
        notLoginnedClientState = new NotLoginnedClientState(this);
        notRegisteredClientState = new NotRegisteredClientState(this);
        readyToReadClientState = new ReadyToReadClientState(this);
        state = notAuthorizedClientState;

        signer = new PSSSigner();
    }

    @Override
    public void run() {
        while (true) {
            try (Selector selector = Selector.open();
                 SocketChannel channel = SocketChannel.open()) {

                ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024 * 2);
                channel.configureBlocking(false);

                SelectionKey selectionKey = channel.register(selector, SelectionKey.OP_CONNECT, buffer);
                selectionKey.attach(buffer);
                channel.connect(new InetSocketAddress("127.0.0.1", 24000));

                int readyChannels;
                while (true) {
                    readyChannels = selector.select();

                    if (readyChannels == 0) continue;

                    Set<SelectionKey> selectedKeys = selector.selectedKeys();

                    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                    while (keyIterator.hasNext()) {

                        SelectionKey key = keyIterator.next();
                        keyIterator.remove();

                        if (key.isConnectable()) {
                            // a connection was established with a remote server.
                            handleConnect(key);
                        } else if (key.isReadable()) {
                            // a channel is ready for reading
                            handleRead(key);
                        } else if (key.isWritable()) {
                            // a channel is ready for writing
                            handleReadyToWrite(key);
                        }
                    }
                }
            } catch (IOException e) {
                if (e instanceof InvalidClientStateException) {
                    InvalidClientStateException exception = (InvalidClientStateException) e;
                    if (exception.getUnfinishedState() == notRegisteredClientState) {
                        signer = new PSSSigner();
                    }
                }
                e.printStackTrace();
            }
        }
    }

    private void handleConnect(SelectionKey key) throws IOException {
        System.out.println("Connection established.");
        SocketChannel channel = (SocketChannel) key.channel();
        channel.finishConnect();
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private void handleRead(SelectionKey key) throws IOException {
        //System.out.println("Socket ready to read.");
        state.readMessage(key);
        key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }

    private void handleReadyToWrite(SelectionKey key) {
        System.out.println("Socket ready to write.");
        state.writeMessage(key);
        key.interestOps(SelectionKey.OP_READ);
    }

    void setState(ClientState state) {
        this.state = state;
    }

    void stateChanged() {
        setChanged();
        notifyObservers();
    }

    ClientState getNotAuthorizedClientState() {
        return notAuthorizedClientState;
    }

    ClientState getNotLoginnedClientState() {
        return notLoginnedClientState;
    }

    ClientState getNotRegisteredClientState() {
        return  notRegisteredClientState;
    }

    ClientState getReadyToReadClientState() {
        return readyToReadClientState;
    }

}
