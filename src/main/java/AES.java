import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.KeyGenerator;

public class AES {
    private byte[] AESKey;

    public AES()
    {
        AESKey = null;
    }

    public void loadKey()
    {
        try {
            FileInputStream file = new FileInputStream(new File("AESKey"));

            int nbrByte = 0;
            int newByte;

            ArrayList<Integer> byteList = new ArrayList<Integer>();
            while ((newByte = file.read()) != -1)
            {
                nbrByte++;
                byteList.add(newByte);
            }

            this.AESKey = new byte[nbrByte];
            for (int i = 0; i < nbrByte; i++)
            {
                this.AESKey[i] = byteList.get(i).byteValue();
            }
            

        } catch (FileNotFoundException e) {
            System.out.println("Couldn't open AESKey file");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Couldn't read byte");
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        // Génération de la clé AES
        KeyGenerator kg;
        try {
            kg = KeyGenerator.getInstance("AES");
            Key key = kg.generateKey();

            FileOutputStream file = new FileOutputStream("AESKey");
            file.write(key.getEncoded());
            file.close();
            
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Unknown algorithm");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't open encryption key file");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Couldn't write to opened key file");
            e.printStackTrace();
        }
    }
}
