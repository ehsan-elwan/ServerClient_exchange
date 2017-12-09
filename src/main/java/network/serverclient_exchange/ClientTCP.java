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
    
             /**
         * @param server String: holding the server name or ip address
	 * @param port int: port number 
         * On a choisi le port 47101 car il n'est attribué.
         * d'apres le site https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers.xhtml
         * de "Service Name and Transport Protocol Port Number Registry Unassigned Port Number"
         * 
         **/

    public ClientTCP(String server, int port) throws IOException,
            NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException {
         /**
         * initializer le client:
	 * initializer le Socket, Input/Output StreamWriter, PrintWriter,BufferedReader
         * generer la clé de la client avec l'algo AES / exporter la clé dans un fichier txt clientKey
         * importer la clé du server qui est generer egalement au lancement du serveur
         * permet le client envoyer un message au serveur
         * 
         **/
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
         /**
         * méthode permettant aux clients d'envoyer des messages au serveur
         * crypter le message avec la clé du serveur
         * ensuite le client attend la reposne du serveur
         * 
         **/
    private void sendMessage() throws IOException {

        String inputmessage = input.nextLine();
        String tosend = encrypt(serverkey, inputmessage);
        pw.println(tosend);
        pw.flush();
        System.out.println("Message sent to the server : " + inputmessage);
        if (inputmessage.equalsIgnoreCase("bye")) {
            System.out.println("Client is closing..");
        } else {
        recevieMessage();
        }

    }
         /**
         * méthode permettant aux clients d'recevoir des messages du serveur
         * decrypter le message avec la clé du client
         * ensuite le client va envoyer sa reposne au serveur
         * 
         **/

    private void recevieMessage() throws IOException {

        String receviedmessage = br.readLine();
        System.out.println("Message received from the server : " + receviedmessage);
        String dec = decrypt(clientkey, receviedmessage);
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

         /**
         * méthode permettant au client d'exporter sa clé dans un ficher txt
         * 
         **/
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
         /**
         * méthode permettant au client d'crypter (asymétrique) les messages à envoyer
         * un moyenne d'améliorer la sécurité de ces échanges est d'ajouter au message
         * un vecteur aléatoire afin de n'a pas savoir il y avait quoi en entrer
         * avant le cryptage "XOR"
         **/
    private String encrypt(SecretKey key, String value) {
        //String initVector = "RandomInitVector";
        //IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
        try {
            
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encrypted = cipher.doFinal(value.getBytes());

            return Base64.encodeBase64String(encrypted);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }
         /**
         * méthode permettant au client d'decrypter les messages qu'il 
         * recoit du serveur avec la clé de client
         **/
    private String decrypt(SecretKey key, String encrypted) {
        try {

            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

         /**
         * méthode permettant au client d'importer la clé de chiffrement de serveur
         * apartir d'un fichier txt "serverKey"
         **/
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
