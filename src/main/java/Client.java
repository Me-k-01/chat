import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Properties;

public class Client {
    public Socket echoSocket = null;
    int port; 
    Socket clientSocket = null;
    DataOutputStream out = null;
    DataInputStream in = null;
    BufferedReader stdIn;
    AES aes;
    Thread listenThread;
    String conAddress;
    int conPort;

    public Client() {
        aes = new AES(); // cryptage AES
        stdIn = new BufferedReader(new InputStreamReader(System.in)); // Entrée utilisateur

        ////////// Config //////////
        Properties prop = new Properties();
        try (FileInputStream fis = new FileInputStream("config.conf")) {
            prop.load(fis); // On charge le fichier config
            conAddress = prop.getProperty("SERVER_ADDRESS").trim(); // Récupérer l'adresse 
            conPort = Integer.parseInt(prop.getProperty("SERVER_PORT").trim()); // Récupérer le port du serveur
            port = Integer.parseInt(prop.getProperty("CLIENT_PORT").trim()); // Récupérer le port du client
        } catch (IOException err) {
            throw new RuntimeException("Fichier config.conf non trouvé.");
        }
        connect();

        /* Thread pour pouvoir écouter les nouveaux messages entrants 
        sans être bloqué par l'entrée utilisateur qui est bloquante  */    
        listenThread = new Thread() { 
            public void run() {
                while ( true ) {
                    try {
                        // Si il y a un nouveau message disponnible
                        if (in.available() > 0) {
                            // On lit le message
                            byte[] received = new byte[in.readInt()];
                            in.read(received);
                            // On le decrypte
                            System.out.print("- Message reçu :\nChiffré : " + Arrays.toString(received));
                            String msg = aes.decryptText(received);
                            // Et on l'affiche à l'utilisateur
                            System.out.println("\nDéchiffré : " + msg);
                        }
                    } catch (SocketException e) {
                        System.out.println("Fin de la communication");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // On arrête d'attendre lorsque le thread est interrompu  
                    try { Thread.sleep(50); } 
                    catch (InterruptedException e) { return; } 
                }
            }
        };
        listenThread.start();

        try {
            write();
        } catch (SocketException e) {
            System.out.println("Arrêt de la connection");
        } catch (IOException e) {
            e.printStackTrace();
        }   
    }
    public void write() throws IOException { // Crypter ce que l'utilisateur écrit et l'envoyer au serveur  
        String usrInput = null;
        ////////// Envoie des entrées utilisateur //////////
        // Tant que l'on a des entrées de l'utilisateur
        while ((usrInput = stdIn.readLine() ) != null && ! usrInput.equals("bye")) { 
            // On crypte le message a envoyer
            byte[] encryptedText = aes.encryptText(usrInput);
            // Et on les envoies au serveur
            out.writeInt(encryptedText.length);
            out.write(encryptedText);
        } 
        ////////// Fermer les streams //////////
        out.close();
        in.close();
        listenThread.interrupt();
    }

    public void connect()  { // Se connecter au serveur
        try {
            ////////// Connection //////////
            echoSocket = new Socket(InetAddress.getByName(conAddress), conPort) ; 
            ////////// Flux I / O //////////
            in  = new DataInputStream( echoSocket.getInputStream());
            out = new DataOutputStream(echoSocket.getOutputStream());
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
        new Client(); 
    }
}
