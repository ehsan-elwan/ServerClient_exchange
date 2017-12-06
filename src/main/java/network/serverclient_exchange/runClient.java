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

    public static void main(String[] args) throws InvalidAlgorithmParameterException {

        try {
            ClientTCP client = new ClientTCP("localhost", 47101);
            //Service Name and Transport Protocol Port Number Registry Unassigned Port Number
         

        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException ex) {
            Logger.getLogger(runClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
