import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Client {
    public Socket echoSocket = null;
    int port; 
    Socket clientSocket = null;
    DataOutputStream out = null;
    DataInputStream in = null;
    BufferedReader stdIn;
    AES aes;

    public Client(int port) {
        this.port = port;
        aes = new AES();
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        startConnect();

        try {
            communicate();
        } catch (SocketException e) {
            System.out.println("Arrêt de la connection");
        } catch (IOException e) {
            e.printStackTrace();
        }   
    }
    public void communicate() throws IOException {
        String usrInput = null;
        ////////// Envoie //////////
        while ((usrInput = stdIn.readLine() ) != null) { // Tant que l'on a des input
            byte[] encryptedText = aes.encryptText(usrInput);
            out.writeInt(encryptedText.length);
            out.write(encryptedText);
            if (usrInput.equals("bye")) { break; }

            if (in.available() > 0) {
                byte[] received = new byte[in.readInt()];
                in.read(received);

                System.out.print("- Message reçu :\nChiffré : " + Arrays.toString(received));
                String msg = this.aes.decryptText(received);
                System.out.println("\nDéchiffré : " + msg);
            }
        } 
        out.close();
        in.close();
    }

    public void startConnect()  {
        int conPort = 4444;
        String conAddress = "192.168.58.75";
        
        try{
            echoSocket = new Socket(InetAddress.getByName(conAddress), conPort) ; 
            out = new DataOutputStream(echoSocket.getOutputStream());
            in = new DataInputStream(echoSocket.getInputStream());
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
