/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.serverclient_exchange;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Ehsan
 */
public class ClientTCP extends Socket {

    private final Socket socket;
    private final OutputStream os;
    private final PrintWriter pw;
    private final OutputStreamWriter osw;
    private final InputStream is;
    private final InputStreamReader isr;
    private final BufferedReader br;
    private final Scanner input;
    private final SecretKey clientkey;
    private final SecretKeySpec serverkey;
    private final Cipher cipher;

    public ClientTCP(String server, int port) throws IOException,
            NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException {

        socket = new Socket(InetAddress.getByName(server), port);
        os = socket.getOutputStream();
        osw = new OutputStreamWriter(os);
        pw = new PrintWriter(osw, true);
        is = socket.getInputStream();
        isr = new InputStreamReader(is);
        br = new BufferedReader(isr);
        input = new Scanner(System.in);
        cipher = Cipher.getInstance("AES");
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        clientkey = keyGenerator.generateKey();
        exportkey();
        serverkey = getServerKey();
        System.out.println("You're now connected to the Server");
        sendMessage();

    }

    private void sendMessage() throws IOException {

        String inputmessage = input.nextLine();
        String tosend = encrypt(clientkey, inputmessage);
        pw.println(tosend);
        pw.flush();
        System.out.println("Message sent to the server : " + inputmessage);
        if (inputmessage.equalsIgnoreCase("bye")) {
            System.out.println("Client closing..");
        } else {
        recevieMessage();
        }

    }

    private void recevieMessage() throws IOException {

        String receviedmessage = br.readLine();
        System.out.println("Message received from the server : " + receviedmessage);
        String dec = decrypt(serverkey, receviedmessage);
        System.out.println("Message dec from the server : " + dec);
        if (dec.equalsIgnoreCase("bye")) {
            System.out.println("Server sent close commande");
        } else {
        sendMessage();
        }
    }

    public Socket getSocket() {
        return this.socket;
    }

    private void exportkey() {

        try {
            byte[] keyBytes = clientkey.getEncoded();
            String encodedKey = new String(Base64.encodeBase64(keyBytes), "UTF-8");
            File file = new File("clientKey");
            System.out.println("My exported code: " + encodedKey);
            PrintWriter writer = new PrintWriter(file, "UTF-8");
            writer.println(encodedKey);
            writer.close();
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            Logger.getLogger(ClientTCP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String encrypt(SecretKey key, String value) {
        //String initVector = "RandomInitVector";
        try {
            //IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            //SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encrypted = cipher.doFinal(value.getBytes());

            return Base64.encodeBase64String(encrypted);
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

    private String decrypt(SecretKey key, String encrypted) {
        try {

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

    private SecretKeySpec getServerKey() {
        BufferedReader brf;
        SecretKeySpec key = null;
        try {
            brf = new BufferedReader(new FileReader("serverKey"));
            String code = brf.readLine();
            brf.close();
            System.out.println("i read code from file: " + code);
            byte[] keyBytes = Base64.decodeBase64(code.getBytes("UTF-8"));
            key = new SecretKeySpec(keyBytes, "AES");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServerTCP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServerTCP.class.getName()).log(Level.SEVERE, null, ex);
        }
        return key;
    }

}
