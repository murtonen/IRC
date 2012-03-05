/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irc;

import java.io.IOException;
import java.util.ArrayList;
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
        private final String newLogLine = "newLogLine";
        private final String newChanMode = "newChanMode";
        private final String newMode = "newMode";
        private final String disconnected = "disconnected";
        private final String privMessage = "privateMessage";
        private final String joinPart = "joinPart";
        
        // Debuggausta varten
        MyServer ircnet;
        
        // Strings
        String topic;
        String channel;
        String activeChannel;
        String logline;
        String response;
        String topicChannel;
        String privateSender;
        String privateMessage;
        int disconnectedServer;
        String mynick;
        String joinPartChannel;
        
        // ServerLista ja servereiden määrä
        MyServer[] serverList;
        Server[] Servers;
        int serverAmount;
        int activeServer;
        
    public IRCModel() throws Exception {
        
        serverAmount = 0;
        activeServer = 0;
        serverList = new MyServer[10];
        logline = "";
        activeChannel = "Default";
        channel = "";
        topic = "";
        response = "";
        topicChannel= "";
        mynick="";
        disconnectedServer=0;
        joinPartChannel="";
        Servers = new Server[50];
        
    }
    
    public void setActiveServer(int index) {
        activeServer = index;
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
    
    public void joinChannel (String nimi, int index) {
        activeChannel = nimi;
        serverList[activeServer].joinChannel(nimi);
        serverList[activeServer].sendRawLine("MODE " + nimi);
        Servers[activeServer].addChannel(index);
    }
    
    public String getLine() {
        return serverList[activeServer].lastText;
    }
    
    public User[] getUsers() {
        return serverList[activeServer].getUsers(activeChannel);
    }

    public User[] getUsersChannel(String channel) {
        return serverList[activeServer].getUsers(channel);
    }
    
    public String getCurrentNick() {
        return serverList[activeServer].getNick();
    }
    
    public void updateTopic(String c, String t) {
        topicChannel = c;
        topic = t;
        setChanged();
        notifyObservers(newTopic);
    }

    public String getTopicChannel() {
        return topicChannel;
    }

    public String getTopic() {
        return topic;
    }

    public void connectToServer(String server, String n) throws IrcException {
        serverList[serverAmount] = new MyServer(this);
        serverList[serverAmount].setVerbose(true);
        serverList[serverAmount].newNick(n);
        mynick=n;
        Servers[serverAmount] = new Server();
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

    public void changeNick(String newnick) {
        serverList[activeServer].newNick(newnick);
    }

    public void reconnect() throws IrcException {
        try {
            serverList[activeServer].reconnect();
        } catch (IOException ex) {
            Logger.getLogger(IRCModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NickAlreadyInUseException ex) {
            Logger.getLogger(IRCModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateMode(String channel, String mode) {
        setChanged();
        notifyObservers(newMode);
    }

    public void kickTarget(String nick) {
        serverList[activeServer].kick(activeChannel, nick);
    }

    public void setActiveChannel(String string) {
        activeChannel = string;
    }

    public void sendLogLine(String line) {
        logline = line;
        setChanged();
        notifyObservers(newLogLine);
    }
    
    public String getLogLine() {
        return logline;
    }

    public void channelModeNotify(String r) {
        response = r;
        setChanged();
        notifyObservers(newChanMode);
    }
    
    public String getResponse() {
        return response;
    }
    
    public void opOrDeop(String n) {
        User[] users = serverList[activeServer].getUsers(activeChannel);
        for (int i = 0;i<users.length;i++) {
            if (users[i].getNick().equals(n)) {
                if (users[i].isOp()) {
                    serverList[activeServer].deOp(activeChannel, n);
                } else {
                    serverList[activeServer].op(activeChannel, n);
                }
            }
        }
    }
    
    public void voiceOrDevoice(String n) {
        User[] users = serverList[activeServer].getUsers(activeChannel);
        for (int i = 0;i<users.length;i++) {
            if (users[i].hasVoice()) {
                serverList[activeServer].deVoice(activeChannel, n);
                } else {
                serverList[activeServer].voice(activeChannel, n);
            }
        }
    }
    
    public void ban(String mask) {
        serverList[activeServer].ban(activeChannel, mask);
    }
    
    public void unban(String mask) {
        serverList[activeServer].unBan(activeChannel, mask);
    }

    public String getActiveChannel() {
        return activeChannel;
    }

    public void partChannel(String channelToPart) {
        serverList[activeServer].partChannel(channelToPart);
    }

    public void disconnectServer(int selectedServer) {
        disconnectedServer = selectedServer;
        setChanged();
        notifyObservers(disconnected);
        serverList[selectedServer].disconnect();
        serverList[selectedServer].dispose();
        serverAmount--;
        
    }
    
    public ArrayList getServerChannels(int selectedServer) {
        return Servers[selectedServer].Channels();
    }

    public void updateActiveServer() {
        activeServer=serverAmount;
    }

    public void privateMessage(String sender, String message) {
        privateSender = sender;
        privateMessage = message;
        setChanged();
        notifyObservers(privMessage);
    }

    public String getSender() {
        return privateSender;
    }

    public String getMessage() {
        return privateMessage;
    }

    public void updateUserListOnJP(String c) {
        joinPartChannel=c;
        setChanged();
        notifyObservers(joinPart);
    }
    
    public String getJoinPartChannel() {
        return joinPartChannel;
    }
}
