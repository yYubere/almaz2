import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by Андрей on 08.04.2014.
 */
public class SocketChannelTest {
    public static void main(String[] argv) throws Exception {
        ByteBuffer buf = ByteBuffer.allocateDirect(1024);
        SocketChannel sChannel = SocketChannel.open();
        sChannel.configureBlocking(false);
        sChannel.connect(new InetSocketAddress("localhost", 24000));
        while (!sChannel.finishConnect());
        int numBytesRead = sChannel.read(buf);

        if (numBytesRead == -1) {
            sChannel.close();
        } else {
            buf.flip();
        }
    }
}
