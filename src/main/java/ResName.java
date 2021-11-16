import java.net.InetAddress;
import java.net.UnknownHostException;

public class ResName {
    public static void main(String[] args) {
        InetAddress address;
        try {
            address = InetAddress.getByName(args[0]);
            System.out.println(args[0] + ":" + address.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
