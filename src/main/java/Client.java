import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Properties;

public class Client implements ActionListener {
    Interface fenetre;
    public Socket echoSocket = null;
    Socket clientSocket = null;
    DataOutputStream out = null;
    DataInputStream in = null;
    BufferedReader stdIn;
    Thread listenThread;

    String conAddress;
    int conPort;
    int port; 
    AES aes;

    public Client() {
        this.fenetre = new Interface(this);
        aes = new AES(); // Crypteur AES
        stdIn = new BufferedReader(new InputStreamReader(System.in)); // Entrée utilisateur
        ////////// Config //////////
        conAddress = Config.get("SERVER_ADDRESS");
        conPort = Config.getInt("SERVER_PORT"); 
        port = Config.getInt("CLIENT_PORT"); 

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

                            fenetre.showMessage.append("- Message reçu :\nChiffré : " + Arrays.toString(received));
                            fenetre.showMessage.append("\nDéchiffré : " + msg + "\n\n");
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
        /*try {
            write(); // Les écritures de l'utilisateur
        } catch (SocketException e) {
            System.out.println("Arrêt de la connection");
        } catch (IOException e) {
            e.printStackTrace();
        }*/  
    }
    // Crypter ce que l'utilisateur écrit et l'envoyer au serveur
    public void write() throws IOException {  
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

     // Se connecter au serveur
    public void connect()  {
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
    @Override
    public void actionPerformed(ActionEvent arg0) {
        String usrInput = fenetre.input.getText();
        fenetre.input.setText("");

        // On crypte le message a envoyer
        byte[] encryptedText = aes.encryptText(usrInput);
        // Et on les envoies au serveur
        try {
            out.writeInt(encryptedText.length);
            out.write(encryptedText);

            if (usrInput.equals("bye")) {
                this.fenetre.dispose();
                out.close();
                in.close();
                listenThread.interrupt();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        
    }
}
