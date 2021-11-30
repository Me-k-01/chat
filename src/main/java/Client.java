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
    Thread listenThread;

    public Client(int port) {
        this.port = port;
        aes = new AES();
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        connect("192.168.4.75", 4444);

        listenThread = new Thread() {
            public void run() {
                while ( true ) {
                    try {
                        if (in.available() > 0) {
                            byte[] received = new byte[in.readInt()];
                            in.read(received);

                            System.out.print("- Message reçu :\nChiffré : " + Arrays.toString(received));
                            String msg = aes.decryptText(received);
                            System.out.println("\nDéchiffré : " + msg);
                        }
                    } catch (SocketException e) {
                        System.out.println("Fin de la communication");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try { Thread.sleep(50); } 
                    catch (InterruptedException e) { return; } // On arrette d'attendre quand on est interrompu  
                }
            }
        };
        listenThread.start();

        try {
            write();
        } catch (SocketException e) {
            System.out.println("Arrêt de la connection");
        } catch (IOException e) {
            e.printStackTrace();
        }   
    }
    public void write() throws IOException {
        String usrInput = null;
        ////////// Envoie //////////
        while ((usrInput = stdIn.readLine() ) != null && ! usrInput.equals("bye")) { // Tant que l'on a des input
            byte[] encryptedText = aes.encryptText(usrInput);
            out.writeInt(encryptedText.length);
            out.write(encryptedText);
        } 
        out.close();
        in.close();
        listenThread.interrupt();
    }

    public void connect(String conAddress, int conPort)  {
        try {
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
