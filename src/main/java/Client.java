import java.io.*;
import java.net.*;
import java.util.Properties;

public class Client extends Server {

    public Client(int port) {
        super(port);
    }
    
    @Override
    public void connect()  {
        String conAddress = "127.0.0.1"; int conPort = 4444;

        Properties prop = new Properties();
        try (FileInputStream fis = new FileInputStream("config.conf")) {
            prop.load(fis);
            conAddress = prop.getProperty("SERVER_ADDRESS");
            conPort = Integer.parseInt(prop.getProperty("SERVER_PORT").trim());
        } catch (IOException err) {
            System.out.println("Fichier config non trouvé.");
        }

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
