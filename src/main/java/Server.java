import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    int port; 
    Socket clientSocket = null;
    ServerSocket serverSocket;

    public Server(int port) {
        this.port = port;
        start() ;
    }    

    public void stop() {
        System.out.println("Stopping server");
        System.exit(-1);
    }
    
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException err) {
            System.out.println("Could not listen on port: " + port);
            stop();
        }
        System.out.println("Listening on port: " + port);
        
        try {
            clientSocket = serverSocket.accept();
            System.out.println("Nouveau Client");
        } catch (IOException err) {
            System.out.println("Accept failed on port: " + port);
            stop();
        }
    }

    public static void main(String[] args) {
        new Server(4444);
    }
}
