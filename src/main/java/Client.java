import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    int port;

    public Client(int port) {
        this.port = port;
        start();
    }

    public void stop() {
        System.out.println("Stopping client");
        System.exit(-1);
    }

    public void start()  {
        int servPort = 3000;
        String serverName = "yoshibox"; // TODO

        try{
            Socket echoSocket = new Socket(serverName, servPort) ; 
            PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),true) ;
            BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream())) ;
        }
        catch (UnknownHostException e){
            System.out.println("Destiation unknown: " + serverName + ":" + port) ;
            stop();
        }
        catch (IOException e){
            System.out.println("Now to investigate this IO issue") ;
            stop();
        }
    }

    public static void main(String[] args) {
        Client client = new Client(3000); 
    }
}
