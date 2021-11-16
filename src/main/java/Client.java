import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    int port;

    public Client(int port) {
        this.port = port;
        start();
    }
    public void start()  {
        int servPort = 3000;
        try{
            Socket echoSocket = new Socket("address", servPort) ; // TODO
            PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),true) ;
            BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream())) ;
            }
            catch (UnknownHostException e){
            System.out.println("Destiation unknown") ;
            System.exit(-1) ;
            }
            catch (IOException e){
            System.out.println("Now to investigate this IO issue") ;
            System.exit(-1) ;
            }
    }

    public static void main(String[] args) {
        Client client = new Client(3000); 
    }
}
