import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    private SecretKey AESKey;
    private Cipher cipher;

    public AES() {
        this.AESKey = null;
        loadKey(); // Chargement automatique de la clé

        try {
            this.cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Algorithm doesn't exist for Cipher");
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public byte[] encryptText(String text) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, this.AESKey);
        } catch (InvalidKeyException e) {
            System.out.println("Non-valid key provided to cipher init (Encryption)");
            e.printStackTrace();
        }

        try { // Faire la gestion d'erreur
            return cipher.doFinal(text.getBytes());
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            return new byte[1];
        } catch (BadPaddingException e) {
            e.printStackTrace();
            return new byte[1];
        }
    }

    public String decryptText(byte[] encryptedText) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, this.AESKey);
        } catch (InvalidKeyException e) {
            System.out.println("Non-valid key provided to cipher init (Decryption)");
            e.printStackTrace();
        }

        try {
            return new String(cipher.doFinal(encryptedText));
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            return new String();
        } catch (BadPaddingException e) {
            e.printStackTrace();
            return new String();
        }
    }

    public void save() {
        try {
            FileOutputStream file = new FileOutputStream("AESKey");
            file.write(AESKey.getEncoded());
            file.close();
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    }
    public void loadKey() {
        try {
            FileInputStream file = new FileInputStream(new File("AESKey"));

            int nbrByte = 0; int newByte;

            ArrayList<Integer> byteList = new ArrayList<Integer>();
            while ((newByte = file.read()) != -1) {
                nbrByte++;
                byteList.add(newByte);
            }

            byte[] aeskeyByte = new byte[nbrByte];

            for (int i = 0; i < nbrByte; i++) {
                aeskeyByte[i] = byteList.get(i).byteValue();
            }

            this.AESKey = new SecretKeySpec(aeskeyByte, "AES");
            

        } catch (FileNotFoundException e) {
            System.out.println("Couldn't open AESKey file");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Couldn't read byte");
            e.printStackTrace();
        }
    }

    public static SecretKey strToKey(String str) {
        // decode the base64 encoded string
        byte[] decodedKey = Base64.getDecoder().decode(str);
        // rebuild key using SecretKeySpec
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
    }

    public void generate() { // Générer une nouvelle clé random 
        KeyGenerator kg;
        try {
            kg = KeyGenerator.getInstance("AES");
            AESKey = kg.generateKey();
        } catch (NoSuchAlgorithmException err) {
            throw new RuntimeException(err);
        }
    }
    private static long stringToSeed(String s) {
        long hash = 0;
        for (char c : s.toCharArray()) {
            hash = 31L*hash + c;
        }
        return hash;
    }
    
    public void generateKeyFromPassword(String password) {  // Générer une nouvelle clé a partir d'un mot de passe
        // decode the base64 encoded string
        Random rd = new Random();
        rd.setSeed(stringToSeed(password));

        byte[] keyBytes = new byte[16];
        rd.nextBytes(keyBytes);
        // rebuild key using SecretKeySpec
        AESKey = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES"); 
    } 

    @Override
    public String toString() {
        // get base64 encoded version of the key
        return Base64.getEncoder().encodeToString(AESKey.getEncoded());
    }   
  
    public static void main(String[] args) {
        AES aes = new AES();
        /////// Générer une nouvelle clé ///////
        //generate();
        //save(); 

        ///////  ///////
        /*
        byte[] test = Base64.getDecoder().decode(aes.toString());
        System.out.println(test.length);
        System.out.println(Arrays.toString(test));
        */
        aes.generateKeyFromPassword("test");
        System.out.println(aes.toString());

        aes.generateKeyFromPassword("test");
        System.out.println(aes.toString());
        aes.generateKeyFromPassword("test");
        System.out.println(aes.toString());

        // Un message "Test" doit
        //System.out.println(aes.decryptText(aes.encryptText("Test")));

    }
}
