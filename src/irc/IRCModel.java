/*
 * Modelissa huolehditaan jarjestelmaan liittyvan datan hallinnasta ja sailytyksesta
 * Lisaksi modeli vastaa kaikesta tiedonvalityksesta pircbot apia extendaavan MyServer luokan toteutuksesta
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
        private final String noServer = "noServer";
        private final String connectOK = "connectOK";
        
        // Debuggausta varten
        MyServer ircnet;
        
        // Connected?
        boolean connected;
        boolean connectedOnce;
        
        // Strings
        String topic;
        String channel;
        String activeChannel;
        String logline;
        String response;
        String topicChannel;
        String privateSender;
        String privateMessage;
        String privateServer;
        String joinPartChannel;
        String fromChannel;
        String fromSender;
        String fromMessage;
        String fromServer;
        String modeServer;
        
               
        // ServerLista ja servereiden määrä sekä muut integerit
        MyServer[] serverList;
        Server[] Servers;
        int serverAmount;
        int activeServer;
        int disconnectedServer;

    // Rakentaja
    public IRCModel() throws Exception {
        
        // Muuttujien alustukset
        serverAmount = 0;
        activeServer = 0;
        
        // Ei kukaan voi kayttaa yli 10 serveria!
        serverList = new MyServer[10];
        Servers = new Server[10];

        // Alustukset jatkuu
        logline = "";
        activeChannel = "Default";
        channel = "";
        topic = "";
        response = "";
        topicChannel= "";
        disconnectedServer=0;
        joinPartChannel="";
        fromChannel = "";
        fromSender = "";
        fromMessage = "";
        connected = false;
        modeServer = "";
        fromServer = "";
        connectedOnce = false;
    }
    
    // Palautetaan tieto siita onko connectoiduttu
    // Tahan voisi kayttaa myos MyServerin metodia...
    public boolean isConnected() {
        return connected;
    }
    
    // Asetetaan aktiivisen serveirn indeksi
    public void setActiveServer(int index) {
        activeServer = index;
    }
    
    // Hihkaistaan observereille että kayttajalista kaipaa paivitysta
    public void updateUserList() {
        setChanged();
        notifyObservers(userChange);
    }
    
    // Saatu uuden tekstirivin tiedot MyServerilta
    // Otetaan ne ylos ja ilmoitetaan observereille etta uutta tekstia saatavilla
    public void newText(String ser,String c, String s, String m) {
        fromServer = ser;
        fromChannel = c;
        fromSender = s;
        fromMessage = m;
        setChanged();
        notifyObservers(newText);
    }
    
    // Metodi jolla valitetaan aktiivisen serverin myserverille tieto siita
    // mille kanavalle pitaa lahettaa tekstirivi
    public void sendLine(String channel,String line) {
        serverList[activeServer].sendMessage(channel,line);
    }
    
    // Metodi kanavalle liittymiseksi, syotteena kanavan nimi ja indeksitieto
    public void joinChannel (String nimi, int index) {
        if (serverAmount > 0) {
        activeChannel = nimi;
        serverList[activeServer].joinChannel(nimi);
        serverList[activeServer].sendRawLine("MODE " + nimi);
        Servers[activeServer].addChannel(index);
        } else {
        // Jos palvelinyhteytta ei ole, ilmoitetaan tasta observoijia
        setChanged();
        notifyObservers(noServer);    
        }
    }
    
    // Haetaan aktiivisen palvelimen viimeisin saama tekstirivi
    public String getLine() {
        return serverList[activeServer].lastText;
    }
    
    // Haetaan aktiivisen palvelimen kayttajat aktiiviselta kanavalta
    public User[] getUsers() {
        return serverList[activeServer].getUsers(activeChannel);
    }

    // Haetaan aktiivisen palvelimen kayttajat parametrina saadulta kanavalta
    public User[] getUsersChannel(String channel) {
        return serverList[activeServer].getUsers(channel);
    }
    
    // Haetaan parametrina saadun palvelimen parametrina saadun kanavan kayttajat
    public User[] getUserChan(String Channel,String server) {
        String serverName="";
        User[] paluu = null;
        for (int i=0;i < serverAmount;i++) {
            serverName = serverList[i].getServer();
            System.out.println(serverName+" vs "+server);
            if (serverName.equals(server)) {
                paluu = serverList[i].getUsers(Channel);
            }
        }       
        return paluu;
    }
    
    // Haetaan parametrina saadun palvelimen parametrina saadun kanavan kayttajat
    // Hieman erilainen vertailu yllaolevaan, equals vs. contains
    public User[] getUsersChannel(String channel,String server) {
        String serverName="";
        User[] paluu = null;
        for (int i=0;i < serverAmount;i++) {
            serverName = serverList[i].getServer();
            if (serverName.contains(server)) {
                paluu = serverList[i].getUsers(channel);
            }
        }
        return paluu;
    }
    
    // Haetaan nickname aktiivisesta palvelimesta
    public String getCurrentNick() {
        return serverList[activeServer].getNick();
    }
    
    // Otetaan ylos saadut tiedot topicista ja kanavasta jolle se asetettiin
    // Ja ilmoitetaan observoijia etta hakevat ja paivittavat nama tiedot
    public void updateTopic(String c, String t) {
        topicChannel = c;
        topic = t;
        setChanged();
        notifyObservers(newTopic);
    }

    // Palautetaan kanava jolla topic muutettiin
    public String getTopicChannel() {
        return topicChannel;
    }

    // Palautetaan topic
    public String getTopic() {
        return topic;
    }

    // Palvelimelle yhdistamismetodi, ottaa syotteena palvelimen nimen ja nicknamen
    // Heittaa IrcExceptionin mikali epaonnistuu
    public void connectToServer(String server, String n) throws IrcException {
        // Asetellaan serveri serverList arrayyn
        serverList[serverAmount] = new MyServer(this);
        // Logitus verboselle
        serverList[serverAmount].setVerbose(true);
        // Asetetaan nick jolla yhdistetaan
        serverList[serverAmount].newNick(n);
        // Luodaan serveri sailomaan kanavatietoja
        Servers[serverAmount] = new Server();
        // Yritetaan yhdistamista
        try {
            serverList[serverAmount].connect(server);
            activeServer=serverAmount;
            serverAmount++;
            connected=true;
            connectedOnce=true;
            setChanged();
            notifyObservers(connectOK);
        } catch (IOException ex) {
            Logger.getLogger(IRCModel.class.getName()).log(Level.SEVERE, null, ex);
        // Jos nick kaytossa, ilmoitetaan observoijia tasta
        } catch (NickAlreadyInUseException ex) {
            setChanged();
            notifyObservers(nickInUse);
        }
    }

    // Asetetaan uusi nickName
    public void changeNick(String newnick) {
        serverList[activeServer].newNick(newnick);
    }

    // Metodi jolla voidaan yrittaa reconnectia aktiivisella serverilla
    public void reconnect() throws IrcException {
        try {
            serverList[activeServer].reconnect();
        } catch (IOException ex) {
            Logger.getLogger(IRCModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NickAlreadyInUseException ex) {
            Logger.getLogger(IRCModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Metodi jolla voidaan potkia henkilo pois aktiiviselta serverilta aktiivisella kanavalla
    public void kickTarget(String nick) {
        nick = nick.replace("+","");
        nick = nick.replace("@","");
        serverList[activeServer].kick(activeChannel, nick);
    }

    // Asetetaan aktiivinen kanava
    public void setActiveChannel(String string) {
        activeChannel = string;
    }

    // Otetaan logline talteen ja ilmoitetaan observoijille etta hakevat ko. tiedon
    public void sendLogLine(String line) {
        logline = line;
        setChanged();
        notifyObservers(newLogLine);
    }
    
    // Palautetaan logline
    public String getLogLine() {
        return logline;
    }

    // Ilmoitetaan etta on saatu uusi kanavamode, parametreina palaute ja serveri jolta tieto saatu
    public void channelModeNotify(String r,String s) {
        response = r;
        modeServer = s;
        setChanged();
        notifyObservers(newChanMode);
    }
    
    // Palautetaan ko. modetieto
    public String getResponse() {
        return response;
    }
    
    // Palautetaan serveri jolla mode tehtiin
    public String getModeServer() {
        return modeServer;
    }
    
    // Metodi jolla asetetaan nicknamelle Operator status, tai poistetaan se jos arvo on jo
    // Parametrina kyseinen nickname
    public void opOrDeop(String n) {
        n = n.replace("+","");
        n = n.replace("@","");
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
    
    // Vastaava metodi jolla asetetaan voice tai poistetaan se jos kyseinen arvo on jo
    public void voiceOrDevoice(String n) {
        n = n.replace("+","");
        n = n.replace("@","");
        User[] users = serverList[activeServer].getUsers(activeChannel);
        for (int i = 0;i<users.length;i++) {
            if (users[i].getNick().equals(n))
            if (users[i].hasVoice()) {
                serverList[activeServer].deVoice(activeChannel, n);
                } else {
                serverList[activeServer].voice(activeChannel, n);
            }
        }   
    }
    
    // Valitetaan tieto bannausmaskista myserverille
    public void ban(String mask) {
        serverList[activeServer].ban(activeChannel, mask);
    }
    
    // Valietaan tieto unbanmaskista myserverille
    public void unban(String mask) {
        serverList[activeServer].unBan(activeChannel, mask);
    }

    // Palautetaan aktiivinen kanava
    public String getActiveChannel() {
        return activeChannel;
    }

    // Metodi kanavalta poistumiseksi, parametrina tieto palvelimesta ja kanavasta
    // seka indeksitieto
    // Kay serverarrayn lapi ja etsii oikean palvelimen ja ilmoittaa sille kanavan jolta poistuttiin
    public void partChannel(String channelToPart, int index) {
        String server = "Default";
        String channel = "Default";
        String serverB = "-1";
        try {
            String[] splitted = channelToPart.split(":");
            server = splitted[0];
            channel = splitted[1];
        } catch (Exception ex) {
            
        }
        for (int i = 0;i < serverAmount;i++) {
            if (serverList[i].getServer() != null ) {
                try {
                    String[] splitmore = serverList[i].getServer().split("\\.");
                    serverB = splitmore[1];
                } catch (Exception ez) {
                    
                }
                if (server.equals(serverB)) {
                    serverList[i].partChannel(channel);
                    Servers[i].removeChannel(index);
                }
            }
        }
    }

    // Metodi jolla disconnectoidutaan palvelimelta, saa syotteena indeksiarvon
    // Disconnectaa ko. serverin ja disposee ilmentyman
    // Ja ilmoittaa observoijille etta on disconnectoiduttu
    public void disconnectServer(int selectedServer) {
        disconnectedServer = selectedServer;
        serverList[selectedServer].disconnect();
        serverList[selectedServer].dispose();
        serverAmount--;
        if ( serverAmount == 0) {
            connected = false;
        }
        setChanged();
        notifyObservers(disconnected);
    }
    
    // Palauttaa ko. palvelimen kanavat
    public ArrayList getServerChannels(int selectedServer) {
        return Servers[selectedServer].Channels();
    }

    // Paivittaa viimeisimman palvelimen aktiiviseksi
    public void updateActiveServer() {
        activeServer=serverAmount;
    }

    // Ottaa privaattiviestin tiedot ylos ja ilmoittaa observoijia etta hakevat ko. tiedot
    public void privateMessage(String ser, String sender, String message) {
        privateSender = sender;
        privateMessage = message;
        privateServer = ser;
        setChanged();
        notifyObservers(privMessage);
    }
    
    // Palauttaa tiedon milta kanavalta viesti oli
    public String fromChannel() {
        return fromChannel;
    }
    
    // Palauttaa tiedon kenelta viesti oli
    public String fromSender() {
        return fromSender;
    }

    // Palauttaa viestin
    public String fromMessage() {
        return fromMessage;
    }
    
    // Palauttaa palvelimen jolta viesit oli
    public String fromServer() {
        return fromServer;
    }
    
    // Palauttaa privaattiviestin lahettajan
    public String getSender() {
        return privateSender;
    }

    // Palauttaa privaattiviestin
    public String getMessage() {
        return privateMessage;
    }

    // Ottaa ylos kanavan joka tulee paivittaa ja ilmoittaa observoijaa tasta
    public void updateUserListOnJP(String c) {
        joinPartChannel=c;
        setChanged();
        notifyObservers(joinPart);
    }
    
    // Palauttaa kanavan jolta partattiin
    public String getJoinPartChannel() {
        return joinPartChannel;
    }

    // Metodi jolla paatellaan voiko reconnectoida
    public boolean canReconnect() {
        boolean paluu;
        if ( serverAmount > 0 && connectedOnce == true) {
            paluu = true;
        } else {
            paluu = false;
        }
        return paluu;
    }

    // Quit metodi disconnectaa ja disposee kaikki myserver oliot
    public void quit() {
        if ( isConnected() ) {
        for (int i=0;i<serverList.length;i++) {
            try {
            serverList[i].disconnect();
            } catch (Exception e) {
                
            }
            serverList[i].dispose();
        }
        }
    }

    // Otetaan ylos palvelin ja kanava jolla usermode muutos tapahtui
    // Ja ilmoitetaan observerille etta hakee ko. tiedot
    public void updateUserListOnMode(String server, String channel, String mode) {
        fromServer = server;
        fromChannel = channel;
        setChanged();
        notifyObservers(newMode);
    }

    // Palauttaa privaattimessagen palvelimen osoitteen
    public String getPServer() {
        return privateServer;
    }
}
