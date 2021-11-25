import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

public class Server extends Thread {
    int port; 
    Socket clientSocket = null;
    DataOutputStream out = null;
    DataInputStream in = null;
    public ServerSocket echoSocket;
    AES aes;

    public Server(int port) {
        super();    
        this.port = port;
        aes = new AES();
        startConnect();

        try {
            communicate();
        } catch (SocketException e) {
            System.out.println("Arrêt de la connection");
        } catch (IOException e) {
            e.printStackTrace();
        }   
    }    

    public void startConnect() {
        try {
            echoSocket = new ServerSocket(port);
        } catch (IOException err) {
            System.out.println("Port occupé: " + port);
            System.exit(-1);
        }
        System.out.println("Le serveur écoute sur le port: " + port);
        
        try {
            clientSocket = echoSocket.accept();
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException err) {
            System.out.println("N'a pas pu accepté de connection");
            err.printStackTrace();
            System.exit(-1);
        }
        System.out.println("Client accepté");
    }
    @Override
    public void run() {
        String msg = "";
        while (! msg.equals("bye")) {
            try {
                if (in.available() > 0) {
                    byte[] received = new byte[in.readInt()];
                    in.read(received);

                    System.out.print("- Message reçu :\nChiffré : " + Arrays.toString(received));
                    msg = this.aes.decryptText(received);
                    System.out.println("\nDéchiffré : " + msg);
                }
            } catch (SocketException e) {
                System.out.println("Fin de la communication");
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void communicate() throws IOException {     
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String usrInput = null;
        start(); // démarage du thread pour la reception    

        while ((usrInput = stdIn.readLine() ) != null && ! usrInput.equals("bye") ) { // Tant que l'on a des input
            byte[] encryptedText = aes.encryptText(usrInput);
            out.writeInt(encryptedText.length);
            out.write(encryptedText);
        } 
        out.close();
        in.close();
        stdIn.close();
        clientSocket.close();
    }

    public static void main(String[] args) {
        new Server(4444);
    }
}
