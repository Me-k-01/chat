import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Client extends WindowAdapter implements ActionListener {
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
        this.fenetre = new Interface(this, this);
        stdIn = new BufferedReader(new InputStreamReader(System.in)); // Entrée utilisateur
        ////////// Config //////////
        Config conf = new Config("client");
        conAddress = conf.get("SERVER_ADDRESS");
        conPort = conf.getInt("SERVER_PORT");
        port = conf.getInt("CLIENT_PORT");
        aes = new AES(conf.get("PASSWORD")); // Crypteur AES
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
                            String msg = aes.decryptText(received);
                            String crypte = new String(received, StandardCharsets.UTF_8); // Message crypté
                            
                            // System.out.print("- Message reçu :\nChiffré : " + crypte);
                            // System.out.println("\nDéchiffré : " + msg);

                            // Et on l'affiche à l'utilisateur
                            fenetre.write("\n - Message reçu : " + msg);
                            fenetre.write("   [Chiffré : \"" + crypte + "\"]");
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
        if (usrInput.isEmpty()) return;
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
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        byte[] encryptedText = aes.encryptText("bye");
        try {
            out.writeInt(encryptedText.length);
            out.write(encryptedText);
        } catch (IOException err) {
            err.printStackTrace();
        }

        System.exit(-1);
    }
}
