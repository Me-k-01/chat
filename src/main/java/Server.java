import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Arrays;

public class Server implements ActionListener {
    int port; 
    DataOutputStream out = null;
    DataInputStream in = null;
    BufferedReader stdIn;
    AES aes;
    Thread listenThread;
    Interface fenetre;
    
    public Server(int port) {
        super();
        this.fenetre = new Interface();
        this.port = port;
        aes = new AES();
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        connect(); // Accepter la prochaine connexion entrante

        // Le thread va lire les messages entrants tant qu'il ne reçoit pas "bye"
        listenThread = new Thread() {
            public void run() { // Réception
                String msg = "";
                while (! msg.equals("bye")) {
                    byte[] received = null;
                    try {
                         // on passe si aucun message n'a été envoyé depuis 
                        if (in.available() <= 0) { continue; }
                        received = new byte[in.readInt()]; // On lit combien de byte contient le prochain message
                        in.read(received); // Puis on lit ce nombre de byte
                    } catch (SocketException e) { 
                        System.out.println("Fin de la communication"); 
                    } catch (IOException e) { 
                        e.printStackTrace();  
                    }

                    System.out.print("- Message reçu :\nChiffré : " + Arrays.toString(received));
                    msg = aes.decryptText(received); // Décryption du texte
                    System.out.println("\nDéchiffré : " + msg);

                    fenetre.showMessage.append(Arrays.toString(received));

                    try { Thread.sleep(50); } 
                    catch (InterruptedException e) { return; } // On arrete d'écouter lorsque l'on est interrompu
                }

            }
        };
        listenThread.start(); // démarrage du thread pour la reception    

        try {
            write(); // Fonction bloquante pour l'envoi de message
        } catch (SocketException e) {
            System.out.println("Arrêt de la connection");
        } catch (IOException e) {
            e.printStackTrace();
        }   
    }    

    public void connect() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port); // Création du socket
        } catch (IOException err) {
            System.out.println("Port occupé: " + port);
            System.exit(-1);
        }
        System.out.println("Le serveur écoute sur le port: " + port);
        
        try {
            Socket echoSocket = serverSocket.accept(); // En attente de connexion
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
            out.writeInt(encryptedText.length); // On écrit la taille du message sortant
            out.write(encryptedText); // Et le contenu du message crypté
            if (usrInput.equals("bye")) { break; }
        } 
        out.close();
        in.close();
        listenThread.interrupt();
    }

    public static void main(String[] args) {
        new Server(4444);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub
        
    }
}
