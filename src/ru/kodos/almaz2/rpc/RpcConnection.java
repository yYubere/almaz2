package ru.kodos.almaz2.rpc;

import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: maslov
 * Date: 24.03.14
 * Time: 13:47
 * To change this template use File | Settings | File Templates.
 */
public class RpcConnection {
    ConnectionState notConnectedClientState;
    ConnectionState connectedClientState;
    ConnectionState notAuthorizedClientState;
    ConnectionState authorizedClientState;
    ConnectionState notRegisteredClientState;
    ConnectionState registeredClientState;
    ConnectionState state;

    Socket socket;

    public RpcConnection () {
        notConnectedClientState = new NotConnectedClientState(this);
        connectedClientState = new ConnectedClientState(this);
        notAuthorizedClientState = new NotAuthorizedClientState(this);
        authorizedClientState = new AuthorizedClientState(this);
        notRegisteredClientState = new NotRegisteredClientState(this);
        registeredClientState = new RegisteredClientState(this);
        state = notConnectedClientState;
    }

    //! Устанавливает TCP/IP соединение с сервером.
    public void connect(String host, int port) {
        state.connectClient(host, port);
        state.authorizeClient();
    }

    //! Проходит авторизацию соединения на сервере.
    public void authorize() {
        state.authorizeClient();
    }

    //! Проходит первичную регистрацию на сервере.
    public void register() {
        state.registerClient();
    }

    void setState(ConnectionState state) {
        this.state = state;
    }

    ConnectionState getNotConnectedClientState() {
        return notConnectedClientState;
    }

    ConnectionState getConnectedClientState() {
        return connectedClientState;
    }

    ConnectionState getNotAuthorizedClientState() {
        return  notAuthorizedClientState;
    }

    ConnectionState getAuthorizedClientState() {
        return authorizedClientState;
    }

    ConnectionState getNotRegisteredClientState() {
        return  notRegisteredClientState;
    }

    ConnectionState getRegisteredClientState() {
        return  registeredClientState;
    }
}
