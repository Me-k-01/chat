import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Server {
    int port;
    Socket echoSocket = null;
    AES aes;

    public Client(int port) {
        super(port);
    }

    @Override
    public void start()  {
        int servPort = 4444;
        String address = "192.168.22.75";

        try{
            echoSocket = new Socket(InetAddress.getByName(address), servPort) ; 
            out = new DataOutputStream(echoSocket.getOutputStream());
            in = new DataInputStream(echoSocket.getInputStream());
        }
        catch (UnknownHostException e) {
            System.out.println("Destiation inconnu: " + address + ":" + servPort) ;
            stop();
        }
        catch (IOException e) {
            e.printStackTrace();
            stop();
        }
    }

    public static void main(String[] args) {
        new Client(4444); 
    }
}
