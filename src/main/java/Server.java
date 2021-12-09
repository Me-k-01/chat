import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    Set<Connexion> connexions;
    Thread readThread;
    int port; 
    AES aes;

    public Server() {
        aes = new AES(); // Crypteur AES
        connexions = new HashSet<Connexion>(); 
        ////////// Config //////////
        Properties prop = new Properties();
        try (FileInputStream fis = new FileInputStream("config.conf")) {
            prop.load(fis); // On charge le fichier config
            port = Integer.parseInt(prop.getProperty("SERVER_PORT").trim()); // Récupérer le port du serveur
        } catch (IOException err) {
            throw new RuntimeException("Fichier config.conf non trouvé.");
        }

        // Thread pour lire ce que les clients envoient au serveur
        readThread = new Thread() {
            public void run() {
                while ( true ) {
                    List<byte[]> msgToBroadcast = readAll(); // Lire tout les clients
                    broadcast(msgToBroadcast); // Retransmettre les messages à tout le monde
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        return; // On arrette d'écouter quand le thread est interrompu
                    } 
                }
            }
        };
        readThread.start();
        listenConnection(); 
        readThread.interrupt();
    }    

    // Attendre des nouvelles connections de clients au serveur
    public void listenConnection() {
        ServerSocket server = null;
        ////////// Creation du serveur //////////
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Le socket du serveur n'a pas pu être ouvert :");
            e.printStackTrace();
        }

        ////////// Écoute //////////
        while (true) {
            Socket newClient = null;
            try {
                // Accepter les nouveaux clients qui se connectent
                newClient = server.accept(); 
            } catch (IOException e) {
                System.out.println("Le serveur n'arrive pas à accepter de nouvelles connexions");
                e.printStackTrace();
            }
            connexions.add(new Connexion(newClient));
            System.out.println("Nouveau client accepté");
        }
        // server.close(); // TODO: Dé-commenter quand on aura la logique de fermeture du serveur
    }

    // Lire les messages que l'on a pu recevoir sur tout les sockets
    public List<byte[]> readAll() { 
        List<byte[]> messages = new ArrayList<byte[]>();
        // On itere avec Iterator pour pouvoir supprimer la connection de la liste si le socket a été fermé
        for (Iterator<Connexion> c = connexions.iterator(); c.hasNext();) { 
            Connexion connexion = c.next();
            try {
                byte[] received = connexion.read(); 
                if (received != null) {
                    messages.add(received);
                    System.out.println("- Message reçu :\nChiffré : " + Arrays.toString(received));
                } 
            } catch (SocketDisconnected err) {
                System.out.println("Deconnexion d'un client!");
                connexion.close();
                c.remove(); // Retirer la connection de la liste
            }
        }
        return messages;
    }

    // Envoyer une liste de messages à tout les clients
    public void broadcast(List<byte[]> msgToBroadcast) {
        for (byte[] msg : msgToBroadcast) {
            for (Iterator<Connexion> c = connexions.iterator(); c.hasNext();) {
                Connexion connexion = c.next();
                try {
                    connexion.send(msg);
                } catch (SocketDisconnected err) {  
                    System.out.println("Deconnexion d'un client!");
                    connexion.close();
                    c.remove(); // Retirer la connection de la liste 
                }
            }
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
