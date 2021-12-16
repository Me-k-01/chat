import java.io.*;
import java.net.*;

public class Client extends Server {
    public Client( ) {
        super(Config.getInt("CLIENT_PORT")); 
    }
    
    @Override
    public void connect()  {
        String conAddress = null; int conPort = 4444;
        conAddress = Config.get("SERVER_ADDRESS");
        conPort = Config.getInt("SERVER_PORT");

        try{
            Socket echoSocket = new Socket(InetAddress.getByName(conAddress), conPort) ; // Connection au serveur
            out = new DataOutputStream(echoSocket.getOutputStream()); // Et ouverture des streams d'E/S
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
        new Client( ); 
    }
}
