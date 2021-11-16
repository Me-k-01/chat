import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    int port; 
    Socket clientSocket = null;
    ServerSocket serverSocket;

    public Server(int port) {
        this.port = port;
        if (! start()) {
            stop();
        }
    }    

    public void stop() {
        System.exit(-1);
    }
    
    public boolean start() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException err) {
            System.out.println("Could not listen on port: " + port);
            return false;
        }
        System.out.println("Listening on port: " + port);
        
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException err) {
            System.out.println("Accept failed on port: " + port);
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        new Server(4444);
    }
}
