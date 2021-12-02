import java.io.*;
import java.net.Socket;

public class Connexion {
    DataOutputStream out;
    DataInputStream in;
    Socket socket;
    
    public Connexion(Socket socket) {
        this.socket = socket;
        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException err) {
            System.out.println("N'a pas pu accepté de connection");
            err.printStackTrace();
            System.exit(-1);
        }
    }    
}
