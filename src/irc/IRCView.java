/*
 * View toteuttaa ohjelman näkymän ja siihen liittyvät toiminnot kuten painikkeet
 * 
 */
package irc;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.User;

    
/**
 *
 * @author Ville Murtonen
 */
public class IRCView extends JPanel implements MouseListener,Observer {
    
        // Paalayoutti
        JFrame frame;
        JTabbedPane chanList;
        
        // JTextFieldit
        JTextField topic,input;
        final JTextField nickN, altN, userN, realN;
                
        // nickList ja modeli sita varten
        DefaultListModel listModel;
        JList nickPanel;
        
        // JmenuBar ja itemit
        JMenuBar menuBar;
        JMenu File,Server,Options;
        JMenuItem Connect,Disconnect,Reconnect,Quit,Preferences;
        
        // Panels
        JPanel northPanel;
        JPanel eastPanel;
        JPanel southPanel;
        JPanel defaultti;
        JPanel westPanel;
        
        // PopUpMenu
        JPopupMenu popup;
        JMenuItem kick,ban,unban,op,voice;
        
        // Model
        IRCModel m;
        
        // Jbutton
        JButton Join,Part,ConnectB,DisconnectB,ReconnectB,QuitB;
        
        // Lists for Channels
        JTextArea[] channels;
        String[] channelNames;
        String[] channelTopics;
        
        // Counts
        int activeTab;
        int chanAmount;
        int serverAmount;
        int selectedServer;
        
        // Other int
        int partChannelIndex;
        
        // ScrollPane
        JScrollPane nickScrollPane,defaultScrollPane;
        
        // MenuButtonGroup
        ButtonGroup serverGroup;
        
        // JButtonGroup
        JRadioButtonMenuItem[] buttonList;
        
        // Strings
        String server, nick, altnick;
        
    public IRCView(IRCModel model) {
        
        // Framen luominen
        frame = new JFrame("(A)wesome irc (C)lient (E)xperience");
        frame.setSize(450,150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
        
        // Serveramount
        serverAmount = 0;
        
        // Asetetaan Observer
        m = model;
        m.addObserver(this);
        
        // JTextFieldit
        topic = new JTextField(40);
        input = new JTextField(40);
        input.setActionCommand("input");
        topic.setEditable(false);
        topic.setText("Welcome to ACE!");
        
        // Nick listing
        listModel = new DefaultListModel();
        nickPanel = new JList(listModel);
        nickPanel.setBackground(topic.getBackground());
        nickPanel.addMouseListener(this);
        nickScrollPane = new JScrollPane(nickPanel);
        
        // JTextArea
        chanAmount = 0;
        channels = new JTextArea[30];
        channelNames = new String[30];
        channels[chanAmount] = new JTextArea(30,30);
        channelNames[chanAmount] = new String("Default");
        channelTopics = new String[30];
        channelTopics[0] = "Default";
        
        // Lisätään vähän tervetuloa defaulttiin.
        channels[0].append("Welcome to Awesome irc Client Experience!\n");
        channels[0].append("Help is not available.\n");
        channels[0].append("But don't panic!\n");
        channels[0].append("Because with such an intuitive UI,\n");
        channels[0].append("no help is needed!\n");
        
        
        // JPanelit, niiden layoutit ja koot
        northPanel = new JPanel(new BorderLayout());
        eastPanel = new JPanel(new BorderLayout());
        eastPanel.setPreferredSize(new Dimension(200,100));
        southPanel = new JPanel(new BorderLayout());
        westPanel = new JPanel();
        westPanel.setLayout(new BoxLayout(westPanel,BoxLayout.Y_AXIS));
        
        // JTabbedPane ja Defaultpane logitusta varten
        chanList = new JTabbedPane();
        defaultti = new JPanel(new BorderLayout());
        defaultti.add(channels[chanAmount]);
        defaultScrollPane = new JScrollPane(defaultti);
        chanList.addTab("Default",defaultScrollPane);
        
        // Asetetaan defaulttab aktiiviseksi
        activeTab = 0;
        
        // Asetetaan default arvoja
        nickN = new JTextField(12);
        altN = new JTextField(12);
        userN = new JTextField(12);
        realN = new JTextField(12);
        nickN.setText("Anonymous123");
        altN.setText("Anonymous234");
        userN.setText("Anonymous");
        realN.setText("John Smith");
       
        // Borderit layouteille
        TitledBorder topicBorder;
        topicBorder = BorderFactory.createTitledBorder("Topic");
        topic.setBorder(topicBorder);
        
        TitledBorder inputBorder;
        inputBorder = BorderFactory.createTitledBorder("Input");
        input.setBorder(inputBorder);
        
        TitledBorder nickListBorder;
        nickListBorder = BorderFactory.createTitledBorder("Names");
        nickPanel.setBorder(nickListBorder);
        
        // Lisataan JPaneliin
        northPanel.add(topic);
        eastPanel.add(nickScrollPane);
        southPanel.add(input);
        
        // Luodaan Menubar
        menuBar = new JMenuBar();
        
        // Asetetaan siihen otsikot
        File = new JMenu("File");
        Server = new JMenu("Server");
        Options = new JMenu("Options");
        menuBar.add(File);
        menuBar.add(Server);
        menuBar.add(Options);
 
        // Ja niihin sisalto
        Connect = new JMenuItem("Connect");
        Disconnect = new JMenuItem("Disconnect");
        Reconnect = new JMenuItem("Reconnect");
        Quit = new JMenuItem("Quit");
        Preferences = new JMenuItem("Preferences");

        // ButtonGroup
        serverGroup = new ButtonGroup();
        buttonList = new JRadioButtonMenuItem[10];
        
        // Ja laitetaan menuun
        File.add(Connect);
        File.add(Disconnect);
        File.add(Reconnect);
        File.add(Quit);
        Options.add(Preferences);
        
        
        // PopUpMenu
        popup = new JPopupMenu();
        kick = new JMenuItem("Kick");
        ban = new JMenuItem("Ban");
        unban = new JMenuItem("UnBan");
        op = new JMenuItem("Op/DeOp");
        voice = new JMenuItem("Voice/DeVoice");
        
        // Lisaykset popupvalikkoon
        popup.add(kick);
        popup.add(ban);
        popup.add(unban);
        popup.add(op);
        popup.add(voice);
        
        // Jbuttonit ja koon pakotukset
        Join = new JButton("Join");
        Join.setPreferredSize(new Dimension(100,25));
        Join.setMinimumSize(new Dimension(100,25));
        Join.setMaximumSize(new Dimension(100,25));
        Part = new JButton("Part");
        Part.setPreferredSize(new Dimension(100,25));
        Part.setMinimumSize(new Dimension(100,25));
        Part.setMaximumSize(new Dimension(100,25));
        ConnectB = new JButton("Connect");
        ConnectB.setPreferredSize(new Dimension(100,25));
        ConnectB.setMinimumSize(new Dimension(100,25));
        ConnectB.setMaximumSize(new Dimension(100,25));
        DisconnectB = new JButton("Disconnect");
        DisconnectB.setPreferredSize(new Dimension(100,25));
        DisconnectB.setMinimumSize(new Dimension(100,25));
        DisconnectB.setMaximumSize(new Dimension(100,25));
        ReconnectB = new JButton("Reconnect");
        ReconnectB.setPreferredSize(new Dimension(100,25));
        ReconnectB.setMinimumSize(new Dimension(100,25));
        ReconnectB.setMaximumSize(new Dimension(100,25));
        QuitB = new JButton("Quit");
        QuitB.setPreferredSize(new Dimension(100,25));
        QuitB.setMinimumSize(new Dimension(100,25));
        QuitB.setMaximumSize(new Dimension(100,25));
        
        // Lisataan ne paneliin
        westPanel.add(ConnectB);
        westPanel.add(DisconnectB);
        westPanel.add(ReconnectB);
        westPanel.add(QuitB);
        westPanel.add(Join);
        westPanel.add(Part);
        
        // Laitetaan frameen Jpanelit
        frame.setJMenuBar(menuBar);
        frame.add(northPanel,BorderLayout.NORTH);
        frame.add(eastPanel,BorderLayout.EAST);
        frame.add(southPanel,BorderLayout.SOUTH);
        frame.add(chanList,BorderLayout.CENTER);
        frame.add(westPanel,BorderLayout.WEST);
        
        // Naytetaan sisalto
        frame.pack();
        frame.setVisible(true);
    }
    
    
    // Mouseeventteja, ide tahtoi nama
    public void mousePressed(MouseEvent e) {
        if ( e.getButton() == MouseEvent.BUTTON3) {
            popup.show( e.getComponent(), e.getX(), e.getY() );
        }
    }
 
    public void mouseReleased(MouseEvent e) {
    }
     
    public void mouseEntered(MouseEvent e) {
    }
     
    public void mouseExited(MouseEvent e) {
    }
     
    public void mouseClicked(MouseEvent e) {
    }
    
    // Update metodissa otetaan observable mallin mukaisesti viesti modelilta
    // Ja toimitaan riippuen viestin arvosta
    public void update(Observable o, Object arg) {
        String msg = (String)arg;
        String channel;
        String topicChannel;
        String topik;
        String newnick;
        // Haetaan modelilta teksti ja syotetaan se nakymaan
        if ( msg.equals("newText")) {
            addToChannel(m.fromServer(),m.fromChannel(),m.fromSender(),m.fromMessage());
        } else if (msg.equals("userChange")) { // Paivitetaan kayttajalistaus
             listModel.clear();
             changeUsers(m.getUsers());
        } else if (msg.equals("newTopic")) { // Haetaan modelista topic ja topickanava ja paivetaan nakyma
            topicChannel = m.getTopicChannel();
            topik = m.getTopic();
            String verrattava = null;
            String verrattavaActive = null;
            for ( int i = 0;i <= chanAmount;i++) {
                if ( channelNames[i] != null ) {
                    try {
                        String[] splitted = channelNames[i].split(" ");
                        verrattava = splitted[1];
                    } catch (Exception ex) {
                        
                    }
                    if (verrattava != null ) {
                    if (verrattava.contains(topicChannel)) {
                        channelTopics[i] = topik;
                    }
                    }
                    try {
                        String[] splitted = channelNames[activeTab].split(" ");
                        verrattavaActive = splitted[1];
                    } catch (Exception ex) {
                        
                    }
                    if (verrattavaActive.contains(topicChannel)) {
                        channelTopics[activeTab]=topik;
                        topic.setText(topik);
                    }
                }
            }
        } else if (msg.equals("nickInUse")) { //Nickname oli kaytossa, kysytaan uusi nick ja reconnectoidaan
            newnick = nickUsed();
            m.changeNick(newnick);
            try {
                m.reconnect();
            } catch (IrcException ex) {
                Logger.getLogger(IRCView.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (msg.equals("newLogLine")) { // Uusi logirivi, haetaan se ja syotetaan nakymaan
            channels[0].append(m.getLogLine()+"\n");
        } else if (msg.equals("newChanMode")) { // Uusi mode kanavalla, haetaan sen tiedot ja syotetaan nakymaan
            String response = m.getResponse();
            String tempServer = m.getModeServer();
            String modeServer=null;
            try {
                String[] splitserver = tempServer.split("\\.");
                modeServer = splitserver[1];
            } catch (Exception ex) {
                
            }        
            String splitted[] = response.split(" ");
            channel = splitted[1];
            String mode = splitted[2];
            String kanava = "-1";
            String palvelin = "-1";
            for ( int j = 0;j <= chanAmount;j++) {
                if (channelNames[j] != null ) {
                    try {
                    String[] split = channelNames[j].split(" ");
                    kanava = split[1];
                    palvelin = split[0];
                    } catch (Exception ex) {
                    }
                if (kanava.contains(channel) && palvelin.contains(modeServer)) {
                    String[] splitmore = chanList.getTitleAt(j).split(" ");
                    String newTopic = splitmore[0]+" "+splitmore[1]+" "+mode;
                    chanList.setTitleAt(j,newTopic);
                }
                }
            }
        // disconnectoiduttu serverilta, poistetaan kaikki ko serverin tabit    
        } else if (msg.equals("disconnected")) {
            serverAmount--;
            int index;
            int remove = this.getSelectedServer();
            ArrayList toBeRemoved=m.getServerChannels(selectedServer);
            for (int k = 0;k < toBeRemoved.size();k++) {
                index = (Integer)toBeRemoved.get(k);
                chanList.remove(index);
            }
            Server.remove(buttonList[remove]);
            JOptionPane.showMessageDialog(frame, "Disconnected from Server!");
            if ( serverAmount != 0 ) 
                buttonList[serverAmount].setSelected(true);
        }  else if ( msg.equals("privateMessage")) { // Privatemessage, haetaan tiedot ja viesti modelista
            String serverP = m.getPServer();
            String serverPr = "Default";
            try {
                String[] splitP = serverP.split("\\.");
                serverPr = splitP[1];
            } catch (Exception ex) {
                
            }
            String sender = m.getSender();
            String message = m.getMessage();
            String vertailtava = "["+serverPr+"]"+" "+sender;
            boolean found = false;
            for (int l = 0;l <= chanAmount;l++) {
                if ( channelNames[l] != null ) {
                    if ( channelNames[l].equals(vertailtava)) {
                        channels[l].append(sender+": "+message+"\n");
                        found = true;
                    }
                }
            }
            if (found == false) {
                this.joinChannel(sender);
                channels[chanAmount].append(sender+": "+message+"\n");
            }
        // Joku joinasi tai parttasi kanavalta, paivitetaan kayttajalistaus modelilta    
        } else if ( msg.equals("joinPart")) {
            listModel.clear();
            changeUsers(m.getUsersChannel(m.getJoinPartChannel()));
        } else if ( msg.equals("connectOK")) { // Connect mennyt oikein annetaan siita popup ilmoitus
            JOptionPane.showMessageDialog(frame, "Connected to Server!");
        } else if ( msg.equals("newMode")) { // Uusi mode, paivitetaan kayttajalistaus
            listModel.clear();
            changeUsers(m.getUserChan(m.fromChannel(),m.fromServer()));
        }
        else {  
        }
        
    }
    
    // Metodi viestin lisaamiseksi kanavalle, ottaa argumenteiksi palvelimen, kanavan
    // lahettajan ja viestin ja parsee tabit lapi etsien oikean tabin ja syottaen sen jalkeen
    // viestin oikein muotoiltuna ko. tabiin
    public void addToChannel(String ser, String c, String s, String m) {
        String kanava = "Default";
        String palvelin = "Default";
        String palvelinB = "";
        try {
            String[] splittwo = ser.split("\\.");
            palvelinB = splittwo[1];
        } catch (Exception ex) {
            
        }
        for ( int i = 0;i <= chanList.getTabCount();i++) {
            if (chanList.getTitleAt(i) != null) {
                try {
                    String[] splitone = chanList.getTitleAt(i).split(" ");
                    
                    palvelin = splitone[0];
                    kanava = splitone[1];
                    palvelin = palvelin.replace("[","");
                    palvelin = palvelin.replace("]","");
                } catch (Exception ez) {
                    
                }
                if (palvelin.equals(palvelinB) && kanava.equals(c)) {
                    channels[i].append(s+": "+m+"\n");
                }
            }
        }
    }
    
    // Yksinkertainen addline jos halutaan vain lisata aktiiviseen tabiin
    public void addLine(String rivi) {
        channels[activeTab].append(rivi+"\n");
    }
    
    // ChangeUsers metodi joka saa syotteena User[] arrayn jonka pohjalta
    // syottaa listModeltiin tavaraa addNick metodilla, muotoiltuaan ensiksi
    // nicknamen eteen tarpeelliset prefixit kuten + tai @
    public void changeUsers(User[] users) {
        if ( users != null ) {
        int koko = users.length;
        String prefix = "";
        for (int i = 0;i<koko;i++) {
            if (users[i].isOp()) {
                prefix = "@";
            } else if ( users[i].hasVoice()) {
                prefix = "+";
            } else {
                prefix = "";
            }
            addNick(users[i].getNick(),prefix);
        }
        }
    }
    
    // Lisaa nicknamen listmodeliin
    public void addNick(String nick,String prefix) {
        String element = prefix+nick;
        listModel.addElement((String)element);
    }
    
    // Listenerit kaikille tarpeellisille, controller injektoi naihin actionlistenerit
    public void setServerItemListener(ActionListener l) {
        buttonList[serverAmount-1].addActionListener(l);
    }
    
    public void setPreferencesListener(ActionListener l) {
        Preferences.addActionListener(l);
    }
    public void setJoinButtonListener(ActionListener l) {
        Join.addActionListener(l); 
    }
    
    public void setPartButtonListener(ActionListener l) {
        Part.addActionListener(l);
    }
    
    public void setInputAreaListener(ActionListener l) {
        input.addActionListener(l);
    }
    
    public void setConnectListener(ActionListener l) {
        Connect.addActionListener(l);
        ConnectB.addActionListener(l);
    }
    
    public void setDisconnectListener(ActionListener l) {
        Disconnect.addActionListener(l);
        DisconnectB.addActionListener(l);
    }
    
    public void setReconnectListener(ActionListener l) {
        Reconnect.addActionListener(l);
        ReconnectB.addActionListener(l);
    }
    
    public void setQuitListener(ActionListener l) {
        Quit.addActionListener(l);
        QuitB.addActionListener(l);
    }
    
    public void setKickListener(ActionListener l) {
        kick.addActionListener(l);
    }
    
    public void setBanListener(ActionListener l) {
        ban.addActionListener(l);
    }
    
    public void setUnBanListener(ActionListener l) {
        unban.addActionListener(l);
    }
    
    public void setOpListener(ActionListener l) {
        op.addActionListener(l);
    }
    public void setVoiceListener(ActionListener l) {
        voice.addActionListener(l);
    }
    
    public void setChanListListener(ChangeListener c) {
        chanList.addChangeListener(c);
    }
    
    public int getSelectedPaneIndex() {
        return chanList.getSelectedIndex();
    }
        
    // Palautetaan inputin teksti ja tyhjataan inputalue
    public String getInput() {
        String paluu = input.getText();
        input.setText("");
        return paluu;
    }
    
    // Metodi kanavatabin luomiseksi, ottaa parametriksi kanavan nimen
    // ja luo taman pohjalta tabin jonka nimi on "[server] #kanava"
    public int joinChannel(String n) {
        String server = "Default";
        int i = this.getSelectedServer();
        String temp = (String)buttonList[i].getText();
        try {
            String[] splitted = temp.split("\\.");
            server = splitted[1];
        } catch (Exception ex) {
        }
        String nimi = "["+server+"] "+n;
        chanAmount++;
        channelNames[chanAmount]=new String(nimi);
        chanList.add(nimi,addPanel()); 
        chanList.setSelectedIndex(chanAmount);
        channels[chanAmount].append("Joined channel: "+nimi+"\n");       
        return chanAmount;
    }
    
    // Metodi joka lisaa palauttaa JTextArean sisaltavan JPanelin
    public JPanel addPanel() {
        channels[chanAmount] = new JTextArea(30,30);
        JScrollPane tempScrollPane = new JScrollPane(channels[chanAmount]);
        JPanel temp = new JPanel(new BorderLayout());
        temp.add(tempScrollPane);
        return temp;
    }
    
    // Palautetaan aktiivinen tabi, miksi sita ei haeta vaan JPanel.getSelectedIndex?
    // en muista enaa syyta...
    public int activeTab() {
        return activeTab;
    }
    
    // Palautetaan aktiivisen tabin kanavan nimi
    public String getActiveChannel() {
        return channelNames[activeTab];
    }

    // Lahetetaan aktiiviselle kanavalle syotetty rivi
    public void sendLine(String input) {
        String nick = m.getCurrentNick();
        channels[activeTab].append(nick+": "+input+"\n");
    }
    
    
    // Kysytaan palvelin JDialogilla
    public String getServer() {
        String s = (String)JOptionPane.showInputDialog(
                    frame,
                    "Enter server to connect to: ","Connect to Server", 1);
        
        return s;
    }
    
    // Kysytaan nicname JDialogilla
    public String getNick() {
        String n = (String)JOptionPane.showInputDialog(
                    frame,
                    "Enter nickname: ","Set Up Nickname", 1);
        
        return n; 
    }
    
    // Kysytaan uusi nickname jos edellinen oli kaytetty
    public String nickUsed() {
        String n = (String)JOptionPane.showInputDialog(
                    frame,
                    "Enter new nickname: ","Nickname already in use", 1);
        
        return n; 
    }
    
    // Lisataan nappi servermenuun ja linkitetaan se buttongrouppiin
    public void addToServerMenu(String server) {
        buttonList[serverAmount] = new JRadioButtonMenuItem(server);
        serverGroup.add(buttonList[serverAmount]);
        buttonList[serverAmount].setSelected(true);
        Server.add(buttonList[serverAmount]);
        serverAmount++;
    }

    // Metodi jolla palautetaan listModelista elementti
    public String getTarget() {
        return (String)listModel.getElementAt(nickPanel.getSelectedIndex());
    }

    // Tyhjataan listModel
    public void clearListModel() {
        listModel.clear();
    }

    // Vaihdettu tabia joten paivitetaan kayttajalistaus nickListiin ko. kanavan osalta
    public void channelChange(int sel) {
        String channel = "Default";
        String server = "Default";
        if (channelTopics[sel] != null ) {
            topic.setText(channelTopics[sel]);
        } else {
            topic.setText("");
        }
        try {
            String[] splitted = channelNames[sel].split(" ");
            server = splitted[0];
            server = server.replace("[","");
            server = server.replace("]","");
            channel = splitted[1];
        } catch (Exception ex) {
            
        }
        changeUsers(m.getUsersChannel(channel,server));
    }
    
    // Paivitetaan ko. kanavan kayttajalistaus hakemalla kayttaja modelista
    public void updateUsers(int sel) {
        changeUsers(m.getUsersChannel(channelNames[sel]));
    }
    
    // Kysytaan netmaski bannausta/unbannausta varten
    public String getMask() {
        String n = (String)JOptionPane.showInputDialog(
                    frame,
                    "Enter netmask","Enter netmask:", 1);
        
        return n; 
    }
    
    // Kysytaan kanava jolle halutaan liittya JDialogilla
    public String getJoinChannel() {
        String n = (String)JOptionPane.showInputDialog(
                    frame,
                    "Enter channel to join: ","Join new channel", 1);
        
        return n; 
    }

    // Asetetaan aktiivinen tabi
    public void setActiveTab(int sel) {
        activeTab=sel;
    }

    // On poistuttu kanavalta joten poistetaan ko. tabi ja palautetaan mikä serveri ja kanava oli kyseessa
    public String partChannel() {
        String serveR = "Default";
        String channeL = "Default";
        String title = chanList.getTitleAt(chanList.getSelectedIndex());
        try {
            String[] splitted = title.split(" ");
            serveR = splitted[0];
            serveR = serveR.replace("[","");
            serveR = serveR.replace("]","");
            channeL = splitted[1];
        } catch (Exception ex) {
            
        }
        chanList.removeTabAt(chanList.getSelectedIndex());
        chanAmount--;
        if (chanAmount > 0) {
        for (int j = 1;j<channelNames.length-1;j++) {
         channelNames[j] = channelNames[j+1];
        }
        }
        String paluu = serveR+":"+channeL;
        return paluu;
    }
    
    // Haetaan buttongroupista valittu palvelin
    public int getSelectedServer() {
        int buttons = serverGroup.getButtonCount();
        int paluu = -1;
        for (int i = 0; i <= buttons; i++) {
            if (buttonList[i] != null ) {
            if (buttonList[i].isSelected()) {
                selectedServer = i;
                paluu = i;
            }
            }
        }
        return paluu;
    }

    // Asetetaan focus inputkentaan
    public void setFocus() {
        input.requestFocusInWindow();
    }

    // Metodi jolla JDialogia hyvaksikayttaen kysytaan kayttajalta preferenssit
    // Ja serializoidaan ne tiedostosta kentiksi ja vastaavasti takaisin tiedostoon
    public void getPreferences() {
            
            // Jbuttonit
            JButton Save = new JButton("Save");
            
            JButton Cancel = new JButton("Cancel");  
            
            // JPanel
            JPanel main = new JPanel(new BorderLayout());
            JPanel buttonPanel = new JPanel();
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
            
            // Paneeliin lisaykset
            buttonPanel.add(Save);
            buttonPanel.add(Cancel);
             
            // Yritetaan ladata talletettuja arvoja
            try {
            ObjectInputStream ois = new ObjectInputStream( new FileInputStream("prefs.obj"));
            
            Preferences p = (Preferences)ois.readObject();

            ois.close();

            // Asetetaan arvot
            nickN.setText(p.getNick());
            altN.setText(p.getAlt());
            userN.setText(p.getUser());
            realN.setText(p.getReal());
            
            } catch ( ClassCastException cce ) {
            } catch (Exception e ) { 
            }

                
            final JDialog dialog = new JDialog(frame, true);
            
            // Tallennetaan tiedot objektiin ja kirjoitetaan objekti fileen
            Save.addActionListener(new ActionListener() {  
                public void actionPerformed(ActionEvent e)  
                {  
                    Preferences myPrefs = new Preferences();
                    if (nickN.getText() != null )
                    myPrefs.setNick(nickN.getText());
                    
                    if (altN.getText() != null)
                    myPrefs.setAlt(altN.getText());
                    
                    if (realN.getText() != null)
                    myPrefs.setReal(realN.getText());
                                        
                    if (userN.getText() != null)
                    myPrefs.setUser(userN.getText());
                    
                    serializeInformation(myPrefs);
                    
                    dialog.dispose();  
                }  
            });
            
            Cancel.addActionListener(new ActionListener() {  
                public void actionPerformed(ActionEvent e)  
                {  
                    dialog.dispose();  
                }  
            });
            
            // Borderit
            // Borderit layouteille
            TitledBorder nickNBorder;
            nickNBorder = BorderFactory.createTitledBorder("Nickname");
            nickN.setBorder(nickNBorder);
            
            TitledBorder altNBorder;
            altNBorder = BorderFactory.createTitledBorder("Alternative nick");
            altN.setBorder(altNBorder);
            
            TitledBorder userNBorder;
            userNBorder = BorderFactory.createTitledBorder("Username");
            userN.setBorder(userNBorder);
            
            TitledBorder realNBorder;
            realNBorder = BorderFactory.createTitledBorder("Realname");
            realN.setBorder(realNBorder);
            
            // Asetellaan ja paketoidaan
            mainPanel.add(nickN);  
            mainPanel.add(altN);  
            mainPanel.add(userN);  
            mainPanel.add(realN);
            main.add(mainPanel,BorderLayout.NORTH);
            main.add(buttonPanel,BorderLayout.SOUTH);
            dialog.setName("Input preferences");
            dialog.getContentPane().add(main);
            dialog.pack();  
            dialog.setLocation(525,200);  
            dialog.setVisible(true);  
    }  
    
    // Serializoidaan tiedostoon, syotteena Pferenssiobjekti
    private void serializeInformation(Preferences p) {  
            try {

            ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream("prefs.obj") );

            oos.writeObject( p );

            oos.close();
        }
        catch (Exception e ) { e.printStackTrace();}
    }

    // Errormessage siita ettei yhteytta ole
    public void noServer() {
        JOptionPane.showMessageDialog(frame, "Connect to a server before joining or parting a channel!");
    }

    // Errormessage siita etta ei voitu reconnectaa
    public void cannotReconnect() {
        JOptionPane.showMessageDialog(frame, "Connect to a server atleast once and disconnect atleast once before attempting to reconnect!");
    }
    
    // Errormessage siita etta ei voitu disconnectaa
    public void cannotDisconnect() {
        JOptionPane.showMessageDialog(frame, "Connect to a server before attempting to discconnect!");
    }

    // Disposetaan frame quitin yhteydessa
    public void quit() {
        frame.dispose();
    }
    
    // Palautetaan indeksiarvo
    public int getIndex() {
        return serverAmount-1;
    }
    
    // Palautetaan chanlistin valitun tabin indeksi
    public int partChannelIndex() {
        return chanList.getSelectedIndex();
    }

}
