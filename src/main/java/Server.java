import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    int port; 
    Socket clientSocket = null;
    ServerSocket serverSocket;

    public Server(int port) {
        this.port = port;
        start();
    }         
    public Server() {
        this.port = 3000;
        start();
    }         
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException err) {
            System.out.println("Could not listen on port: " + port);
            System.exit(-1);
        }
        System.out.println("Listening on port: " + port);
        
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException err) {
            System.out.println("Accept failed on port: " + port);
            System.exit(-1);
        }
    }
    public static void main(String[] args) {
        new Server(4444);
    }
}
