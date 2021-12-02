import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class Server {
    int port; 
    Socket clientSocket = null;
    DataOutputStream out = null;
    DataInputStream in = null;
    BufferedReader stdIn;
    AES aes;
    List<Socket> echoSockets; 

    public Server(int port) {
        this.port = port;
        aes = new AES();
        echoSockets = new ArrayList<Socket>(); 
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        startConnect();

        read();
    }    

    public void startConnect() {
        try {
            echoSocket = new ServerSocket(port);
        } catch (IOException err) {
            System.out.println("Port occupé: " + port);
            System.exit(-1);
        }
        System.out.println("Le serveur écoute sur le port: " + port);
        
        try {
            clientSocket = echoSocket.accept();
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException err) {
            System.out.println("N'a pas pu accepté de connection");
            err.printStackTrace();
            System.exit(-1);
        }
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
