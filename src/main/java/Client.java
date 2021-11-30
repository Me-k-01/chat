import java.io.*;
import java.net.*;
import java.util.Properties;

public class Client extends Server {

    public Client(int port) {
        super(port);
    }

    @Override
    public void connect()  {
        String conAddress = "192.168.4.75";
        int conPort = 4444;

        Properties prop = new Properties();
        String fileName = "config.conf";
        try (FileInputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
        } catch (IOException err) {
            err.printStackTrace();
        }
        conAddress = (String)prop.get("SERVER_ADDRESS");
        conPort = (Integer)prop.get("SERVER_PORT");

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
