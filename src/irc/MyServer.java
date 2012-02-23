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
        IRCView v;
        
    public MyServer(IRCView view) {
        v = view;
        this.setName("WildeBot");
    }
    
    public void onMessage(String channel, String sender,
                       String login, String hostname, String message) {
        v.addText(message);
    }
    
    public void onUserList(String channel, User[] users) {
        int koko = users.length;
        String user = "";
        for ( int i = 0; i < koko;i++) {
            users[i].getNick()
        }
        
    }
}
