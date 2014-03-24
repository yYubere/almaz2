import ru.kodos.client.Almaz2Client;

/**
 * Created with IntelliJ IDEA.
 * User: maslov
 * Date: 24.03.14
 * Time: 14:40
 * To change this template use File | Settings | File Templates.
 */
public class Almaz2ClientTest {
    public static void main(String[] args) {
        if (args.length == 2) {
            Almaz2Client client = new Almaz2Client();
            String host = args[0];
            int port = Integer.parseInt(args[1]);
            client.connect(host, port);
        }
    }
}
