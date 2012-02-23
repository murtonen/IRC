/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irc;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author Ville Murtonen
 */
public class IRC {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    IRCModel m = new IRCModel();
                    IRCView v = new IRCView(m);
                    IRCController c = new IRCController(m,v);
                } catch (Exception ex) {
                    Logger.getLogger(IRC.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        });
    }
}
