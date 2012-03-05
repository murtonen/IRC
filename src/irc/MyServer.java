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
        this.setName("Abc12345");
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
    
    public void onTopic(String channel, String topic, String setBy, long date, boolean changed) {
        m.updateTopic(channel,topic);
    }
    
    public void newNick(String nick) {
        this.setName(nick);   
    }
    
    public void onMode(String channel,
                      String sourceNick,
                      String sourceLogin,
                      String sourceHostname,
                      String mode) {
        m.updateMode(channel,mode);
        m.updateUserList();
    }
    
    public void log(String line) {
        m.sendLogLine(line);
    }
    
    public void onJoin(String channel, String sender, String login, String hostname) {
        if (!sender.equals(this.getNick()))
        m.updateUserListOnJP(channel);
    }
    
    public void onPart(String channel, String sender, String login, String hostname) {
        if (!sender.equals(this.getNick()))
        m.updateUserListOnJP(channel);
    }
    
    public void onUserMode(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String mode) {
        m.updateUserList();
    }
            
    public void onServerResponse(int code, String response) {
        if ( code == 324 ) {
            m.channelModeNotify(response);
        }
    }
    
    public void onPrivateMessage(String sender, String login, String hostname, String message) {
        m.privateMessage(sender,message);
    }
    
    
}
