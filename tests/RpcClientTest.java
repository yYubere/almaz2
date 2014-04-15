import ru.kodos.almaz2.client.Almaz2Client;
import ru.kodos.almaz2.rpc.RPCClient;

import java.util.Observable;
import java.util.Observer;

/**
 * Created with IntelliJ IDEA.
 * User: maslov
 * Date: 24.03.14
 * Time: 14:40
 * To change this template use File | Settings | File Templates.
 */
public class RpcClientTest {
    class RpcClientObserver implements Observer {
        RPCClient rpc;

        public RpcClientObserver() {
            rpc = new RPCClient();
            rpc.addObserver(this);
        }

        public void go() {
            rpc.run();
        }

        @Override
        public void update(Observable o, Object arg) {
            System.out.println("update fired");
        }
    }
    public static void main(String[] args) {
        if (args.length == 2) {
            Almaz2Client client = new Almaz2Client();
//            String host = args[0];
//            int port = Integer.parseInt(args[1]);
//            client.connect(host, port);
        }
//        System.out.println("Client is started.");
//        InputStreamReader is = new InputStreamReader(System.in);
//        BufferedReader reader = new BufferedReader(is);
//        String message;
//
//        try {
//            while ((message = reader.readLine()) != null) {
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
