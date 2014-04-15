package ru.kodos.almaz2.rpc;

import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * Created with IntelliJ IDEA.
 * User: maslov
 * Date: 24.03.14
 * Time: 13:38
 * To change this template use File | Settings | File Templates.
 */
public interface ClientState {
    public void writeMessage(SelectionKey key);

    public void readMessage(SelectionKey key) throws IOException;
}
