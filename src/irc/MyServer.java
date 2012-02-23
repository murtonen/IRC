/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irc;
import org.jibble.pircbot.*;

/**
 *
 * @author Ville Murtonen
 */
public class MyServer extends PircBot {
        IRCModel m;
        String lastText;
        String lastChannel;
        
    public MyServer(IRCModel model) {
        m = model;
        this.setName("WildeBot");
    }
    
    public void onMessage(String channel, String sender,
                       String login, String hostname, String message) {
        lastText = sender+": "+message;
        m.newText();
    }
    
    public void onUserList(String channel, User[] users) {
        lastChannel = channel;
        m.updateUserList();
    }
}
