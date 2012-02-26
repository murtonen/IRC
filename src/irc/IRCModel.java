/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irc;

import java.io.IOException;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.User;

/**
 *
 * @author Ville Murtonen
 */
public class IRCModel extends Observable {
    
        // Muuttujat teksteille
        private final String userChange = "userChange";
        private final String newText = "newText";
        private final String newTopic = "newTopic";
        private final String nickInUse = "nickInUse";
        
        // Debuggausta varten
        MyServer ircnet;
        
        // Topic ja Channel
        String topic;
        String channel;
        
        // ServerLista ja servereiden määrä
        MyServer[] serverList;
        int serverAmount;
        int activeServer;
        
    public IRCModel() throws Exception {
        
        serverAmount = 0;
        activeServer = 0;
        serverList = new MyServer[10];
        
        // Now start our bot up.
        //ircnet = new MyServer(this);
        
        // Enable debugging output.
        //ircnet.setVerbose(true);
        
        // Connect to the IRC server.
        //ircnet.connect("irc.inet.fi");
        
        // Join the #pircbot channel.
        // ircnet.joinChannel("#omatkanavat");
    }
    
    public void updateUserList() {
        setChanged();
        notifyObservers(userChange);
    }
    
    public void newText() {
        setChanged();
        notifyObservers(newText);
    }
    
    public void sendLine(String channel,String line) {
        serverList[activeServer].sendMessage(channel,line);
    }
    
    public void joinChannel (String nimi) {
        serverList[activeServer].joinChannel(nimi);
    }
    
    public String getLine() {
        return serverList[activeServer].lastText;
    }
    
    public User[] getUsers() {
        return serverList[activeServer].getUsers(serverList[activeServer].lastChannel);
    }

    public User[] getUsersChannel(String channel) {
        return serverList[activeServer].getUsers(channel);
    }
    
    public String getCurrentNick() {
        return serverList[activeServer].getNick();
    }
    
    void updateTopic(String c, String t) {
        channel = c;
        topic = t;
        setChanged();
        notifyObservers(newTopic);
    }

    String getTopicChannel() {
        return channel;
    }

    String getTopic() {
        return topic;
    }

    public void connectToServer(String server, String n) throws IrcException {
        System.out.println("Connecting to server!");
        String nick = n;
        serverList[serverAmount] = new MyServer(this);
        serverList[serverAmount].setVerbose(true);
        serverList[serverAmount].newNick(n);
        try {
            serverList[serverAmount].connect(server);
        } catch (IOException ex) {
            Logger.getLogger(IRCModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NickAlreadyInUseException ex) {
            setChanged();
            notifyObservers(nickInUse);
        }
        activeServer=serverAmount;
        serverAmount++;
    }

    void changeNick(String newnick) {
        serverList[activeServer].newNick(newnick);
    }

    void reconnect() throws IrcException {
        try {
            serverList[activeServer].reconnect();
        } catch (IOException ex) {
            Logger.getLogger(IRCModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NickAlreadyInUseException ex) {
            Logger.getLogger(IRCModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
