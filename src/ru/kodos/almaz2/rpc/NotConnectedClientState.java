package ru.kodos.almaz2.rpc;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created with IntelliJ IDEA.
 * User: maslov
 * Date: 24.03.14
 * Time: 13:52
 * To change this template use File | Settings | File Templates.
 */
public class NotConnectedClientState implements ConnectionState {
    RpcConnection rpcConnection;

    public NotConnectedClientState(RpcConnection rpcConnection) {
        this.rpcConnection = rpcConnection;
    }

    @Override
    public void connectClient(String host, int port) {
        try {
            rpcConnection.socket = new Socket(host, port);
        }
        catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            if (rpcConnection.socket.isConnected()) {
                rpcConnection.setState(rpcConnection.getConnectedClientState());
            }
        }
    }

    @Override
    public void authorizeClient() {
        //To change body of implemented methods use File | Settings | File Templates.
        System.out.println("Client is not connected");
    }

    @Override
    public void registerClient() {
        //To change body of implemented methods use File | Settings | File Templates.
        System.out.println("Client is not connected");
    }
}
