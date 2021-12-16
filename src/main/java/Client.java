import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.util.Arrays;

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
                            System.out.print("- Message reçu :\nChiffré : " + Arrays.toString(received));
                            System.out.println("\nDéchiffré : " + msg);
                            // Et on l'affiche à l'utilisateur
                            fenetre.showMessage.append("Message reçu : " + msg + "\n");
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
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    public void windowClosing(WindowEvent e)
    {
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
