import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketException;

public class ReadThread extends Thread
{
    DataInputStream in;
    AES aes;

    public ReadThread(DataInputStream dis, AES aes)
    {
        super();
        this.in = dis;
        this.aes = aes;
    }

    @Override
    public void run()
    {
        while (true)
        {
            try {
                if (in.available() > 0)
                {
                    byte[] received = new byte[in.readInt()];
                    in.read(received);

                    System.out.print("- Message reçu :\nChiffré : ");
                    for (byte b: received)
                    {
                        System.out.print(b + " ");
                    }
                    String msg = this.aes.decryptText(received);
                    System.out.println("\nDéchiffré : " + msg);
                    if (msg.equals("bye")) {
                        break;
                    }
                }
            } catch (SocketException e) {
                System.out.println("Fin de la communication");
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
