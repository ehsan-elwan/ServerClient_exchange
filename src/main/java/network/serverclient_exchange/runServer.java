/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.serverclient_exchange;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author Ehsan
 */
public class runServer {
    
         /**
         * 
	 * Simulate a server with TCP/IP Protocol listening to port 47101 waiting for connection.
	 *
	 **/
    
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException {
         /**
	 * @param port int: numero de port
         * On a choisi le port 47101 car il n'est attribué.
         * d'apres le site https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers.xhtml
         * de "Service Name and Transport Protocol Port Number Registry Unassigned Port Number"
         * 
         **/
         int port = 47101;
       
        try {
            // créer un serveur 
            ServerTCP server = new ServerTCP(port);
            // chercher une connection 
            server.getConnection();
                  
        } catch (IOException ex) {
            Logger.getLogger(runServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
