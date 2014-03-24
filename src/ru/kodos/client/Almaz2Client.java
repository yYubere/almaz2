package ru.kodos.client;

import ru.kodos.rpc.Rpc;

/**
 * Created with IntelliJ IDEA.
 * User: maslov
 * Date: 24.03.14
 * Time: 13:23
 * To change this template use File | Settings | File Templates.
 */
public class Almaz2Client {
    Rpc rpc;

    public Almaz2Client() {
        rpc = new Rpc();
    }

    public void connect(String host, int port) {
        rpc.connect(host, port);
    }
}
