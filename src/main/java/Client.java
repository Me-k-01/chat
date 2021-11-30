import java.io.*;
import java.net.*;

public class Client extends Server {

    public Client(int port) {
        super(port);
    }

    @Override
    public void connect()  {
        String conAddress = "192.168.58.75";
        int conPort = 4444;
        try{
            Socket echoSocket = new Socket(InetAddress.getByName(conAddress), conPort) ; 
            out = new DataOutputStream(echoSocket.getOutputStream());
            in = new DataInputStream(echoSocket.getInputStream());
        } catch (UnknownHostException e) {
            System.out.println("Destination inconnu: " + conAddress + ":" + conPort) ;
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
