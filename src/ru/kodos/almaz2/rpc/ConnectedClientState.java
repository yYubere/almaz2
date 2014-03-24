package ru.kodos.almaz2.rpc;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: maslov
 * Date: 24.03.14
 * Time: 14:06
 * To change this template use File | Settings | File Templates.
 */
public class ConnectedClientState implements ConnectionState {
    RpcConnection rpcConnection;

    public ConnectedClientState(RpcConnection rpcConnection) {
        this.rpcConnection = rpcConnection;
    }

    @Override
    public void connectClient(String host, int port) {
        //To change body of implemented methods use File | Settings | File Templates.
        System.out.println("Client already connected.");
    }

    @Override
    public void authorizeClient() {
        // Формируем команду авторизации.
        // Выдаем команду серверу.
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(rpcConnection.socket.getOutputStream())
            );
            bufferedWriter.write("hello!");

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(rpcConnection.socket.getInputStream())
            );

            char[] buffer = new char[1024];
            int len = 0;
            len = bufferedReader.read(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        }
        // Если сервер ответил положительно, то переходим в состояние ClientAuthorizedState
        rpcConnection.setState(rpcConnection.getAuthorizedClientState());
        // Если сервер сообщил, что необходимо произвести первичную авторизацию,
        // то переходим в состояние NotRegisteredClientState
        //rpcConnection.setState(rpcConnection.getNotRegisteredClientState());
        // Если другие ошибки, то закрываем сокет и переходим в состояние NotConnectedClientState
    }

    @Override
    public void registerClient() {
        //To change body of implemented methods use File | Settings | File Templates.
        System.out.println("Хотите странного.");
    }
}
