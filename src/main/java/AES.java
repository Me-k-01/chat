import java.io.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class AES {
    private SecretKey AESKey;
    private Cipher cipher;

    public AES()
    {
        this.AESKey = null;
        loadKey(); // Chargement automatique de la clé

        try { // Instantiation du crypteur
            this.cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Algorithm doesn't exist for Cipher");
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            System.out.println("???????");
            e.printStackTrace();
        }
    }

    public byte[] encryptText(String text)
    {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, this.AESKey); // Mise en mode encryption avec notre clé AES
        } catch (InvalidKeyException e) {
            System.out.println("Non-valid key provided to cipher init (Encryption)");
            e.printStackTrace();
        }

        try {
            return cipher.doFinal(text.getBytes()); // Encryptage du texte
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            return new byte[1];
        } catch (BadPaddingException e) {
            e.printStackTrace();
            return new byte[1];
        }
    }

    public String decryptText(byte[] encryptedText)
    {
        try {
            cipher.init(Cipher.DECRYPT_MODE, this.AESKey); // Mise en mode décryption avec notre clé AES
        } catch (InvalidKeyException e) {
            System.out.println("Non-valid key provided to cipher init (Decryption)");
            e.printStackTrace();
        }

        try {
            return new String(cipher.doFinal(encryptedText)); // Décryption du texte
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            return new String();
        } catch (BadPaddingException e) {
            e.printStackTrace();
            return new String();
        }
    }


    public void loadKey()
    {
        try {
            // Chargement du fichier au est stocké la clé
            FileInputStream file = new FileInputStream(new File("AESKey"));

            int nbrByte = 0;
            int newByte;

            // Lecture du fichier 
            ArrayList<Integer> byteList = new ArrayList<Integer>();
            while ((newByte = file.read()) != -1) {
                nbrByte++;
                byteList.add(newByte);
            }

            byte[] aeskeyByte = new byte[nbrByte];

            // Mettre la clé dans une forme sous laquelle elle peut être chargée
            for (int i = 0; i < nbrByte; i++) {
                aeskeyByte[i] = byteList.get(i).byteValue();
            }

            // Chargement de la clé
            this.AESKey = new SecretKeySpec(aeskeyByte, "AES");
            

        } catch (FileNotFoundException e) {
            System.out.println("Couldn't open AESKey file");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Couldn't read byte");
            e.printStackTrace();
        }
    }

    public static void generateAESKey()
    {
        KeyGenerator kg; // Instantiation du générateur de clé
        try {
            kg = KeyGenerator.getInstance("AES"); // en mode AES
            SecretKey key = kg.generateKey();

            FileOutputStream file = new FileOutputStream("AESKey"); // Ouverture du fichier pour stocké la nouvelle clé
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

    public static void main(String[] args) {
        //generateAESKey();

        // Un message "Test" doit apparaitre dans le terminal pour vérifier que le passage par
        // l'encryption et la décryption fonctionne
        AES aes = new AES();
        System.out.println(aes.decryptText(aes.encryptText("Test")));
    }
}
