import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    int port; 
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
            ServerSocket serverSocket = new ServerSocket(port);
        } catch (IOException err) {
            System.out.println("Could not listen on port" + Integer.toString(port));
        }
    }
    public static void main(String[] args) {
        new Server(4444);
    }
}
