package ru.kodos.rpc;

/**
 * Created with IntelliJ IDEA.
 * User: maslov
 * Date: 24.03.14
 * Time: 13:38
 * To change this template use File | Settings | File Templates.
 */
public interface ConnectionState {
    public void connectClient(String host, int port);
    public void authorizeClient();
    public void registerClient();
}
