/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irc;

import java.util.Observable;
import org.jibble.pircbot.User;

/**
 *
 * @author Ville Murtonen
 */
public class IRCModel extends Observable {
        private final String userChange = "userChange";
        private final String newText = "newText";
        MyServer ircnet;
        
    public IRCModel() throws Exception {
        // Now start our bot up.
        ircnet = new MyServer(this);
        
        // Enable debugging output.
        ircnet.setVerbose(true);
        
        // Connect to the IRC server.
        ircnet.connect("irc.inet.fi");
        
        // Join the #pircbot channel.
        ircnet.joinChannel("#omatkanavat");
    }
    
    public void updateUserList() {
        setChanged();
        notifyObservers(userChange);
    }
    
    public void newText() {
        setChanged();
        notifyObservers(newText);
    }
    
    public void joinChannel (String nimi) {
        ircnet.joinChannel("#"+nimi);
    }
    
    public String getLine() {
        return ircnet.lastText;
    }
    
    public User[] getUsers() {
        return ircnet.getUsers(ircnet.lastChannel);
    }
}
