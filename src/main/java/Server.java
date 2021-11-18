import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    int port; 
    Socket clientSocket = null;
    PrintWriter out = null;
    BufferedReader in = null;
    Socket echoSocket = null;
    ServerSocket serverSocket;

    public Server(int port) {
        this.port = port;
        start() ;
        try {
            listen();
        } catch (IOException e) {
            e.printStackTrace();
        }    
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
    public void listen() throws IOException {
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String userInput;
        while ((userInput = stdIn.readLine() ) != null) {
            out.println(userInput);
            System.out.println("echo: " + in.readLine());
        } 
        out.close();
        in.close();
        stdIn.close();
        echoSocket.close();
    }

    public static void main(String[] args) {
        new Server(4444);
    }
}
