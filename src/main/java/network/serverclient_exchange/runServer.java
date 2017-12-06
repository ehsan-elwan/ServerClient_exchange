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
    
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException {
        
       
        try {
            ServerTCP server = new ServerTCP(47101);
            //Service Name and Transport Protocol Port Number Registry Unassigned Port Number
            server.getConnection();
            
           
        } catch (IOException ex) {
            Logger.getLogger(runServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
