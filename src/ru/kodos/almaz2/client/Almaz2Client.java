package ru.kodos.almaz2.client;

import ru.kodos.almaz2.rpc.RPCClient;

import java.util.Observable;
import java.util.Observer;

/**
 * Created with IntelliJ IDEA.
 * User: maslov
 * Date: 24.03.14
 * Time: 13:23
 * To change this template use File | Settings | File Templates.
 */
public class Almaz2Client implements Observer {
    RPCClient rpc;

    public Almaz2Client() {
        rpc = new RPCClient();
        rpc.addObserver(this);
        rpc.connect("127.0.0.1", 24000);
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("update fired");
        if (o instanceof RPCClient) {
            RPCClient rpcClient = (RPCClient) o;
            Integer errorCode = (Integer) arg;

            switch (errorCode) {
                case 0:
                    break;
                case 1004:
                    rpcClient.register("1", "primarykey");
                    break;
            }
        }
    }
}
