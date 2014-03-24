package ru.kodos.rpc;

/**
 * Created with IntelliJ IDEA.
 * User: maslov
 * Date: 24.03.14
 * Time: 14:07
 * To change this template use File | Settings | File Templates.
 */
public class RegisteredClientState implements ConnectionState {
    RpcConnection rpcConnection;

    public RegisteredClientState(RpcConnection rpcConnection) {
        this.rpcConnection = rpcConnection;
    }

    @Override
    public void connectClient(String host, int port) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void authorizeClient() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void registerClient() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
