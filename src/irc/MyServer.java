/*
 * MyServer olio on PircBotin laajennus joka toteuttaa palvelimen
 * 
 */
package irc;
import org.jibble.pircbot.*;

/**
 *
 * @author Ville Murtonen
 */
public class MyServer extends PircBot {
        // Muuttujat
        IRCModel m;
        String lastText;
        String lastChannel;
    
    // Rakentaja
    public MyServer(IRCModel model) {
        
        m = model;
        
        // Asetetaan placeholder nimi jos jotain jossain menee mönkään...
        this.setName("Abc12345");
    }
    
    // Korvataan onMessage toteutus ja ohjataan sen tiedot modelille
    public void onMessage(String channel, String sender,
                       String login, String hostname, String message) {
        String server = this.getServer();
        m.newText(server,channel,sender,message);
    }
    
    // Korvataan onUserList ja ohjataan sen tiedot modelille
    public void onUserList(String channel, User[] users) {
        lastChannel = channel;
        m.updateUserList();
    }
   
    // Korvataan onTopic ja ohjataan sen tiedot modelille
    public void onTopic(String channel, String topic, String setBy, long date, boolean changed) {
        m.updateTopic(channel,topic);
    }
    
    // Korvataan newNick ja ohjataan sen tiedot modelille
    public void newNick(String nick) {
        this.setName(nick);   
    }
    
    // Korvataan onMode ja ohjataan sen tiedot modelille
    public void onMode(String channel,
                      String sourceNick,
                      String sourceLogin,
                      String sourceHostname,
                      String mode) {
        m.updateUserListOnMode(this.getServer(),channel,mode);
    }
    
    // Korvataan log ja ohjataan sen tiedot modelille
    public void log(String line) {
        m.sendLogLine(line);
    }
    
    // Korvataan onJoin ja ohjataan sen tiedot modelille
    public void onJoin(String channel, String sender, String login, String hostname) {
        if (!sender.equals(this.getNick()))
        m.updateUserListOnJP(channel);
    }
    
    // Korvataan onPart ja ohjataan sen tiedot modelille
    public void onPart(String channel, String sender, String login, String hostname) {
        if (!sender.equals(this.getNick()))
        m.updateUserListOnJP(channel);
    }
    
    // Custom onServerResponse toteutus koodille 324, jolla saadaan kanavamoodit kiinni
    public void onServerResponse(int code, String response) {
        if ( code == 324 ) {
            String server = this.getServer();
            m.channelModeNotify(response,server);
        }
    }
    
    // Korvataan onPrivateMessage toteutus ja ohjataan sen tiedot modelille
    public void onPrivateMessage(String sender, String login, String hostname, String message) {
        String server = this.getServer();
        m.privateMessage(server,sender,message);
    }
    
    
}
