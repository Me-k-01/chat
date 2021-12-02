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
    Thread readThread;

    public Server(int port) {
        this.port = port;
        aes = new AES();
        connexions = new HashSet<Connexion>();
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        readThread = new Thread() {
            public void run() {
                while ( true ) {
                    read();
                    
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        break; // On arrette d'ecouter
                    } 
                }
            }
        };
        listenConnection();
    }    

    public void listenConnection() {
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Le socket du serveur n'a pas pu être ouvert :");
            e.printStackTrace();
        }
        while (true)
        {
            Socket newClient = null;
            try {
                newClient = server.accept();
            } catch (IOException e) {
                System.out.println("Le serveur n'arrive pas à accepter de nouvelles connexions");
                e.printStackTrace();
            }
            connexions.add(new Connexion(newClient));

            System.out.println("Nouveau client accepté");
        }

        // server.close(); // Dé-commenter quand on aura la logique de fermeture du serveur
    }

    public void read() {
        String msg = "";
        for (Connexion connexion : connexions) {
            try {
                if (connexion.in.available() <= 0) { continue; }

                byte[] received = new byte[connexion.in.readInt()];
                connexion.in.read(received);

                System.out.print("- Message reçu :\nChiffré : " + Arrays.toString(received));
                //msg = this.aes.decryptText(received);
                //System.out.println("\nDéchiffré : " + msg);
            } catch (SocketException e) {
                System.out.println("Fin de la communication");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcast(byte[] msg) {

        for (Connexion connexion : connexions) {
            connexion.out.writeInt(msg.length);
            connexion.out.write(msg);
        }
    }

    public static void main(String[] args) {
        new Server(4444);
    }
}
