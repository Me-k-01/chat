import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    int port; 
    Socket clientSocket = null;
    PrintWriter out = null;
    BufferedReader in = null;
    ServerSocket serverSocket;

    public Server(int port) {
        this.port = port;
        start() ;
        System.out.println("Connecté à un client");
        try {
            communicate();
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
            out = new PrintWriter(clientSocket.getOutputStream(),true) ;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())) ;
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
            out.println(userInput);
            System.out.println("echo: " + in.readLine());
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
