import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class Server {
    int port; 
    BufferedReader stdIn;
    AES aes;
    Set<Connexion> connexions;

    Thread listenThread;

    public Server(int port) {
        this.port = port;
        aes = new AES();
        connexions = new HashSet<Connexion>();
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        read();
        listenConnection();
    }    

    public void listenConnection() {
        try {
            echoSocket = new ServerSocket(port);
        } catch (IOException err) {
            System.out.println("Port occupé: " + port);
            System.exit(-1);
        }
        System.out.println("Le serveur écoute sur le port: " + port);
        
        clientSocket = echoSocket.accept();
        System.out.println("Client accepté");
    }

    public void read() {
        String msg = "";
        while ( true ) {
            try {
                if (in.available() > 0) {
                    byte[] received = new byte[in.readInt()];
                    in.read(received);

                    System.out.print("- Message reçu :\nChiffré : " + Arrays.toString(received));
                    msg = this.aes.decryptText(received);
                    System.out.println("\nDéchiffré : " + msg);
                }
            } catch (SocketException e) {
                System.out.println("Fin de la communication");
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                break; // On arrette d'ecouter
            } 
        }
    }

    public static void main(String[] args) {
        new Server(4444);
    }
}
