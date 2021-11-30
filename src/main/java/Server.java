import java.io.*;
import java.net.*;
import java.util.Arrays;

public class Server {
    int port; 
    DataOutputStream out = null;
    DataInputStream in = null;
    BufferedReader stdIn;
    AES aes;
    Thread listenThread;
    
    public Server(int port) {
        super();    
        this.port = port;
        aes = new AES();
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        connect();

        listenThread = new Thread() {
            public void run() { // Réception
                String msg = "";
                while (! msg.equals("bye")) {
                    byte[] received = null;
                    try {
                         // on passe si aucun message n'a été envoyé depuis 
                        if (in.available() <= 0) { continue; }
                        received = new byte[in.readInt()];
                        in.read(received);
                    } catch (SocketException e) { 
                        System.out.println("Fin de la communication"); 
                    } catch (IOException e) { 
                        e.printStackTrace();  
                    }

                    System.out.print("- Message reçu :\nChiffré : " + Arrays.toString(received));
                    msg = aes.decryptText(received);
                    System.out.println("\nDéchiffré : " + msg);

                    try { Thread.sleep(50); } 
                    catch (InterruptedException e) { return; } // On arrette d'écouter lorsque l'on est interrompu
                }

            }
        };
        listenThread.start(); // démarage du thread pour la reception    

        try {
            write();
        } catch (SocketException e) {
            System.out.println("Arrêt de la connection");
        } catch (IOException e) {
            e.printStackTrace();
        }   
    }    

    public void connect() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException err) {
            System.out.println("Port occupé: " + port);
            System.exit(-1);
        }
        System.out.println("Le serveur écoute sur le port: " + port);
        
        try {
            Socket echoSocket = serverSocket.accept();
            out = new DataOutputStream(echoSocket.getOutputStream());
            in = new DataInputStream(echoSocket.getInputStream());
        } catch (IOException err) {
            System.out.println("N'a pas pu accepté de connection");
            err.printStackTrace();
            System.exit(-1);
        }
        System.out.println("Client accepté");
    }
    public void write() throws IOException {
        String usrInput = null;
        ////////// Envoie //////////
        while ((usrInput = stdIn.readLine() ) != null) { // Tant que l'on a des input
            byte[] encryptedText = aes.encryptText(usrInput);
            out.writeInt(encryptedText.length);
            out.write(encryptedText);
            if (usrInput.equals("bye")) { break; }
        } 
        out.close();
        in.close();
        listenThread.interrupt();
    }

    public static void main(String[] args) {
        new Server(4444);
    }
}
