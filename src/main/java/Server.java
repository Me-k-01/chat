import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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
                    List<byte[]> msgToBroadcast = readAll();
                    broadcast(msgToBroadcast);
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
        while (true) {
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

    public List<byte[]> readAll() {
        List<byte[]> messages = new ArrayList<byte[]>();

        for (Connexion connexion : connexions) {
            try {
                byte[] received = connexion.read(); 
                if (received != null) {
                    messages.add(received);
                    System.out.print("- Message reçu :\nChiffré : " + Arrays.toString(received));
                } 
            } catch (SocketDisconnected err) {
                System.out.println("Deconnexion d'un client!");
                connexions.remove(connexion);
            }
        }
        return messages;
    }

    public void broadcast(List<byte[]> msgToBroadcast) {
        for (byte[] msg : msgToBroadcast) {
            for (Connexion connexion : connexions) {
                try {
                    connexion.send(msg);
                } catch (SocketDisconnected err) {
                    System.out.println("Deconnexion d'un client!");
                    connexions.remove(connexion);
                }
            }
        }
    }

    public static void main(String[] args) {
        new Server(4444);
    }
}
