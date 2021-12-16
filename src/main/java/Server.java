import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Arrays;

public class Server implements ActionListener {
    int port; 
    DataOutputStream out = null;
    DataInputStream in = null;
    AES aes;
    Thread listenThread;
    Interface fenetre;
    
    public Server() {
        aes = new AES();
        this.port = Config.getInt("SERVER_PORT");
        connect();  
        this.fenetre = new Interface(this);
        read();
    }    
    public Server(int port) {
        aes = new AES();
        this.port = port;
        connect();  
        this.fenetre = new Interface(this);
        read();
    }    

    public void read() { // Lire les messages entrant
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

                    fenetre.showMessage.append("- Message reçu :\nChiffré : " + Arrays.toString(received));
                    fenetre.showMessage.append("\nDéchiffré : " + msg + "\n\n");

                    try { Thread.sleep(50); } 
                    catch (InterruptedException e) { return; } // On arrete d'écouter lorsque l'on est interrompu
                }
                fenetre.dispose();
            }
        };
        listenThread.start(); // démarrage du thread pour la reception    
    }

    public void connect() { // Accepter la prochaine connexion entrante
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

    @Override
    public void actionPerformed(ActionEvent arg0) {
        String usrInput = fenetre.input.getText();
        fenetre.input.setText("");

        byte[] encryptedText = aes.encryptText(usrInput);
        try {
            out.writeInt(encryptedText.length);
            out.write(encryptedText); // Et le contenu du message crypté
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // On écrit la taille du message sortant

        if (usrInput.equals("bye")) {
            this.fenetre.dispose();
            listenThread.interrupt();
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
