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

    public byte[] read() throws SocketDisconnected {
        if (socket.isClosed()) throw new SocketDisconnected();
        byte[] msg = null;

        try {
            if (in.available() > 0) {
                msg = new byte[in.readInt()];
                in.read(msg);
            }
        } catch (IOException e) {
            throw new SocketDisconnected();
        }

        return msg;
    }

    public void send(byte[] msg) throws SocketDisconnected {
        if (socket.isClosed()) throw new SocketDisconnected();

        try {
            out.writeInt(msg.length);
            out.write(msg);
        } catch (IOException e) {
            throw new SocketDisconnected();
        }
    }

    public void close() {
        try {
            in.close();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
