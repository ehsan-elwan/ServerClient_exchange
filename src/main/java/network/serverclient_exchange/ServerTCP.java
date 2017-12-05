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
import static java.lang.Thread.sleep;
import java.net.ServerSocket;
import java.net.Socket;
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
public class ServerTCP extends ServerSocket {

    private final ServerSocket serverSocket;
    private Socket client;
    private InputStream is;
    private InputStreamReader isr;
    private BufferedReader br;
    private SecretKeySpec clientkey;
    private final SecretKey serverkey;
    private OutputStream os;
    private OutputStreamWriter osw;
    private final KeyGenerator keyGenerator;
    private PrintWriter pw;
    private final Scanner input;

    public ServerTCP(int port) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException {
        this.serverSocket = new ServerSocket(port);
        input = new Scanner(new InputStreamReader(System.in));
        keyGenerator = KeyGenerator.getInstance("AES");
        serverkey = keyGenerator.generateKey();
        exportkey();

    }

    public void getConnection() {
        try {
            System.out.println("Waiting for new client!");
            client = serverSocket.accept();
            is = client.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            os = client.getOutputStream();
            osw = new OutputStreamWriter(os);
            pw = new PrintWriter(osw, true);
            sleep(500);
            clientkey = getClientKey();
            System.out.println("Client has connected!");
            getMessage();

        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ServerTCP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closeConnection() {
        try {
            client.close();
            is.close();
            isr.close();
            br.close();
            System.out.println("Connection is closed");
            getConnection();
        } catch (IOException ex) {
            Logger.getLogger(ServerTCP.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void sendMessage() {
        String inputMessage;
        inputMessage = input.nextLine();
        String tosend = encrypt(serverkey, inputMessage);
        pw.println(tosend);
        pw.flush();
        System.out.println("Message sent to the client : " + inputMessage);
        if (inputMessage.equalsIgnoreCase("bye")) {
            System.out.println("sending close commande");
            closeConnection();
        } else {
        getMessage();
        }
    }

    private void getMessage() {
        String msg = "";
        String decmsg="";
        byte[] result;
        try {
            msg = br.readLine();
            System.out.println("Crypted Message from client is " + msg);
            decmsg = decrypt(clientkey, msg);
            System.out.println("Deccrypted Message from client is " + decmsg);

        } catch (IOException ex) {
            Logger.getLogger(ServerTCP.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (decmsg.equalsIgnoreCase("bye")) {
            System.out.println("Client has signed out!");
            closeConnection();
            
        } else {
        sendMessage();
        }

    }

    private SecretKeySpec getClientKey() {
        BufferedReader brf;
        SecretKeySpec key = null;
        try {
            brf = new BufferedReader(new FileReader("clientKey"));
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

    private String encrypt(SecretKey key, String value) {
        try {

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

    private void exportkey() {

        try {
            byte[] keyBytes = serverkey.getEncoded();
            String encodedKey = new String(Base64.encodeBase64(keyBytes), "UTF-8");
            File file = new File("serverKey");
            System.out.println("My exported code: " + encodedKey);
            PrintWriter writer = new PrintWriter(file, "UTF-8");
            writer.println(encodedKey);
            writer.close();
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            Logger.getLogger(ClientTCP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
