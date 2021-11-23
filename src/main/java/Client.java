import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Server {
    int conPort = 4444;
    String conAddress = "192.168.22.75";
    public Socket echoSocket = null;

    public Client(int port) {
        super(port);
    }

    @Override
    public void startConnect()  {
        try{
            echoSocket = new Socket(InetAddress.getByName(conAddress), conPort) ; 
            out = new DataOutputStream(echoSocket.getOutputStream());
            in = new DataInputStream(echoSocket.getInputStream());
        } catch (UnknownHostException e) {
            System.out.println("Destiation inconnu: " + conAddress + ":" + conPort) ;
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        System.out.println("Connecté au serveur");
    }

    public static void main(String[] args) {
        new Client(4444); 
    }
}
