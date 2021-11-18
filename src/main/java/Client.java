import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    int port;
    PrintWriter out = null;
    BufferedReader in = null;
    Socket echoSocket = null;

    public Client(int port) {
        this.port = port;
        start();
        System.out.println("Connecté au serveur");
        try {
            communicate();
        } catch (IOException e) {
            e.printStackTrace();
        }    
    }

    public void start()  {
        int servPort = 4444;
        String address = "192.168.22.75";
        try{
            echoSocket = new Socket(InetAddress.getByName(address), servPort) ; 
            out = new PrintWriter(echoSocket.getOutputStream(),true) ;
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream())) ;
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
    public void communicate() throws IOException {
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String userInput;
        while ((userInput = stdIn.readLine() ) != null) { // Tant que l'on a des input
            out.println(userInput);
            System.out.println("echo: " + in.readLine());
        } 
        out.close();
        in.close();
        stdIn.close();
        echoSocket.close();
    }

    public void stop() {
        System.out.println("Arrêt du client");
        System.exit(-1);
    }

    public static void main(String[] args) {
        Client client = new Client(4444); 
    }
}
