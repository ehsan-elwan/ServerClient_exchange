/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.serverclient_exchange;


import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author Ehsan
 */
public class runClient {
    	/**
         * 
	 * Simulate a client connection with TCP/IP Protocol.
	 *
	 **/

    public static void main(String[] args) throws InvalidAlgorithmParameterException {
         /**
         * @param server String: holding the server name or ip address
	 * @param port int: port number 
         * On a choisi le port 47101 car il n'est attribué.
         * d'apres le site https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers.xhtml
         * de "Service Name and Transport Protocol Port Number Registry Unassigned Port Number"
         * 
         **/
        String server = "localhost";
        int port = 47101;
        try {
            // créer un client 
            ClientTCP client = new ClientTCP(server, port);

        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException ex) {
            Logger.getLogger(runClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
