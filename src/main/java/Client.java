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

    public Client(int port) {
        this.port = port;
        aes = new AES();
        stdIn = new BufferedReader(new InputStreamReader(System.in));

        // Récuperer l'adresse et le port du server dans le fichier config
        Properties prop = new Properties();
        try (FileInputStream fis = new FileInputStream("config.conf")) {
            prop.load(fis);
            conAddress = prop.getProperty("SERVER_ADDRESS");
            conPort = Integer.parseInt(prop.getProperty("SERVER_PORT").trim());
        } catch (IOException err) {
            System.out.println("Fichier config non trouvé.");
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
    public void write() throws IOException {
        String usrInput = null;
        ////////// Envoie //////////
        // Tant que l'on a des entrées de l'utilisateur
        while ((usrInput = stdIn.readLine() ) != null && ! usrInput.equals("bye")) { 
            // On les cryptes
            byte[] encryptedText = aes.encryptText(usrInput);
            // Et on les envoies
            out.writeInt(encryptedText.length);
            out.write(encryptedText);
        } 
        // Fermer les streams
        out.close();
        in.close();
        listenThread.interrupt();
    }

    public void connect()  {
        try {
            // Se connecter au serveur
            echoSocket = new Socket(InetAddress.getByName(conAddress), conPort) ; 
            // I / O
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
        new Client(4444); 
    }
}
