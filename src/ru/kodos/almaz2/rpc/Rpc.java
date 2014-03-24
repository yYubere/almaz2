package ru.kodos.almaz2.rpc;

import java.net.*;
import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: maslov
 * Date: 24.03.14
 * Time: 13:24
 * To change this template use File | Settings | File Templates.
 */
public class Rpc {
    RpcConnection rpcConnection;

    public Rpc() {
        rpcConnection = new RpcConnection();
    }

    public void connect(String host, int port) {
        rpcConnection.connect(host, port);
    }
}
