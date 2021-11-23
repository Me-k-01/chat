import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {
    int port; 
    Socket clientSocket = null;
    DataOutputStream out = null;
    DataInputStream in = null;
    ServerSocket serverSocket;
    AES aes;

    public Server(int port) {
        this.port = port;
        start() ;
        System.out.println("Connecté à un client");
        this.aes = new AES();

        try {
            communicate();
        } catch (SocketException e) {
            System.out.println("Arrêt de la connection");
        } catch (IOException e) {
            e.printStackTrace();
        }   
    }    

    public void stop() {
        System.out.println("Arrêt du serveur");
        System.exit(-1);
    }
    
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException err) {
            System.out.println("Port occupé: " + port);
            stop();
        }
        System.out.println("Écoute sur le port: " + port);
        
        try {
            clientSocket = serverSocket.accept();
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException err) {
            System.out.println("N'a pas pu accepté de connection");
            err.printStackTrace();
            stop();
        }
    }
    public void communicate() throws IOException {
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String userInput;
        while ((userInput = stdIn.readLine() ) != null) { // Tant que l'on a des input
            byte[] encryptedText = aes.encryptText(userInput);

            out.writeInt(encryptedText.length);
            out.write(encryptedText);

            if (in.available() > 0)
            {
                byte[] received = new byte[in.readInt()];
                in.read(received);

                System.out.print("- Message reçu :\nChiffré : ");
                for (byte b: received)
                {
                    System.out.print(b + " ");
                }
                String msg =  aes.decryptText(received);
                System.out.println("\nDéchiffré : " + msg);
                if (msg.equals("bye")) {
                    break;
                }
            }
            if (userInput.equals("bye")) {
                break;
            }
        } 
        out.close();
        in.close();
        stdIn.close();
        clientSocket.close();
        stop();
    }

    public static void main(String[] args) {
        new Server(4444);
    }
}
