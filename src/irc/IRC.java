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
                IRCView v = new IRCView();
                IRCController c = new IRCController(v);
                
                try {
                    IRCModel m = new IRCModel(v);
                } catch (Exception ex) {
                    Logger.getLogger(IRC.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        });
    }
}
