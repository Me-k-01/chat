import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    int port;
    DataOutputStream out = null;
    DataInputStream in = null;
    Socket echoSocket = null;
    AES aes;

    public Client(int port) {
        this.port = port;
        start();
        System.out.println("Connecté au serveur");
        this.aes = new AES();
        
        try {
            communicate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start()  {
        int servPort = 4444;
        String address = "192.168.22.75";
        //String address = "127.0.0.1";
        try{
            echoSocket = new Socket(InetAddress.getByName(address), servPort) ; 
            out = new DataOutputStream(echoSocket.getOutputStream());
            in = new DataInputStream(echoSocket.getInputStream());
        }
        catch (UnknownHostException e) {
            System.out.println("Destiation inconnu: " + address + ":" + servPort) ;
            stop();
        }
        catch (IOException e) {
            e.printStackTrace();
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
            }
            if (userInput.equals("bye")) {
                break;
            }
        } 
        out.close();
        in.close();
        stdIn.close();
        echoSocket.close();
        stop();
    }

    public void stop() {
        System.out.println("Arrêt du client");
        System.exit(-1);
    }

    public static void main(String[] args) {
        Client client = new Client(4444); 
    }
}
