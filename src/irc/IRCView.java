/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
        JTextField topic,input;
        JTabbedPane chanList;
        JFrame frame;
        
        // nickList
        DefaultListModel listModel;
        JList nickPanel;
        
        // JmenuBar
        JMenuBar menuBar;
        JMenu File,Server;
        JMenuItem Connect,Disconnect,Reconnect,Quit;
        
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
        
        // ScrollPane
        JScrollPane nickScrollPane,defaultScrollPane;
        
        // MenuButtonGroup
        ButtonGroup serverGroup;
        
        // JButtonGroup
        JRadioButtonMenuItem[] buttonList;
        
    public IRCView(IRCModel model) {
        
        // Framen luominen
        frame = new JFrame("(A)wesome irc (C)lient (E)xperience");
        frame.setSize(450,150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
        
        // Serveramount
        serverAmount = 0;
        
        // Asetetaan observer
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
        
        
        // JPanelit
        northPanel = new JPanel(new BorderLayout());
        eastPanel = new JPanel(new BorderLayout());
        eastPanel.setPreferredSize(new Dimension(200,100));
        southPanel = new JPanel(new BorderLayout());
        westPanel = new JPanel();
        westPanel.setLayout(new BoxLayout(westPanel,BoxLayout.Y_AXIS));
        
        // JTabbedPane
        chanList = new JTabbedPane();
        defaultti = new JPanel(new BorderLayout());
        defaultti.add(channels[chanAmount]);
        defaultScrollPane = new JScrollPane(defaultti);
        chanList.addTab("Default",defaultScrollPane);
        activeTab = 0;
        
       
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
        menuBar.add(File);
        menuBar.add(Server);
 
        // Ja niihin sisalto
        Connect = new JMenuItem("Connect");
        Disconnect = new JMenuItem("Disconnect");
        Reconnect = new JMenuItem("Reconnect");
        Quit = new JMenuItem("Quit");

        // ButtonGroup
        serverGroup = new ButtonGroup();
        buttonList = new JRadioButtonMenuItem[10];
        
        // Ja laitetaan menuun
        File.add(Connect);
        File.add(Disconnect);
        File.add(Reconnect);
        File.add(Quit);
        
        
        // PopUpMenu
        popup = new JPopupMenu();
        kick = new JMenuItem("Kick");
        ban = new JMenuItem("Ban");
        unban = new JMenuItem("UnBan");
        op = new JMenuItem("Op/DeOp");
        voice = new JMenuItem("Voice/DeVoice");
                
        popup.add(kick);
        popup.add(ban);
        popup.add(unban);
        popup.add(op);
        popup.add(voice);
        
        // Jbutton
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
    
    public void update(Observable o, Object arg) {
        String msg = (String)arg;
        String channel;
        String topicChannel;
        String topik;
        String newnick;
        // TODO: TÄMÄ EI TOIMI, LÄHETTÄÄ KAIKKI ACTIVETABIIN!!!
        if ( msg.equals("newText")) {
            addLine(m.getLine());
        } else if (msg.equals("userChange")) {
            System.out.println("Cleared listmodel");
            listModel.clear();
            changeUsers(m.getUsers());
        } else if (msg.equals("newTopic")) {
            topicChannel = m.getTopicChannel();
            topik = m.getTopic();
            for ( int i = 0;i <= chanAmount;i++) {
                if ( channelNames[i] != null ) {
                    if (topicChannel.equals(channelNames[i])) {
                        channelTopics[i] = topik;
                    }
                    if (topicChannel.equals(channelNames[activeTab])) {
                        channelTopics[activeTab]=topik;
                        topic.setText(topik);
                    }
                }
            }
        } else if (msg.equals("nickInUse")) {
            newnick = nickUsed();
            m.changeNick(newnick);
            try {
                m.reconnect();
            } catch (IrcException ex) {
                Logger.getLogger(IRCView.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (msg.equals("newLogLine")) {
            channels[0].append(m.getLogLine()+"\n");
        } else if (msg.equals("newChanMode")) {
            String response = m.getResponse();
            String splitted[] = response.split(" ");
            channel = splitted[1];
            String mode = splitted[2];
            for ( int j = 0;j <= chanAmount;j++) {
                if (channelNames[j] != null ) {
                }
                if (channel.equals(channelNames[j])) {
                    String newTopic = channel+" "+mode;
                    chanList.setTitleAt(j,newTopic);
                }
            }
        } else if (msg.equals("disconnected")) {
            int index;
            int remove = this.getSelectedServer();
            ArrayList toBeRemoved=m.getServerChannels(selectedServer);
            for (int k = 0;k < toBeRemoved.size();k++) {
                index = (Integer)toBeRemoved.get(k);
                chanList.remove(index);
            }
            Server.remove(buttonList[remove]);
            buttonList[serverAmount].setSelected(true);
        }  else if ( msg.equals("privateMessage")) {
            String sender = m.getSender();
            String message = m.getMessage();
            boolean found = false;
            for (int l=0;l < chanAmount;l++) {
                if ( channelNames[l] != null ) {
                    if ( channelNames[l].equals(sender)) {
                        channels[l].append(sender+": "+message+"\n");
                        found = true;
                    }
                }
            }
            if (found == false) {
                this.joinChannel(sender);
                channels[chanAmount].append(sender+": "+message+"\n");
            }
        } else if ( msg.equals("joinPart")) {
            System.out.println("Cleared listmodel");
            listModel.clear();
            changeUsers(m.getUsersChannel(m.getJoinPartChannel()));
        } else {
            //System.out.println(msg);
        }
    }
    
    public void addLine(String rivi) {
        channels[activeTab].append(rivi+"\n");
    }
    
    public void changeUsers(User[] users) {
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
    
    public void addNick(String nick,String prefix) {
        String element = prefix+nick;
        System.out.println("Added to ListModel: "+element);
        listModel.addElement((String)element);
        
    }
    
    // Listenerit kaikille tarpeellisille
    
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
        
    public String getInput() {
        String paluu = input.getText();
        input.setText("");
        return paluu;
    }
    
    public int joinChannel(String nimi) {
        chanAmount++;
        channelNames[chanAmount]=new String(nimi);
        chanList.add(nimi,addPanel()); 
        chanList.setSelectedIndex(chanAmount);
        channels[chanAmount].append("Joined channel: "+nimi+"\n");       
        return chanAmount;
    }
    
    public JPanel addPanel() {
        channels[chanAmount] = new JTextArea(30,30);
        JScrollPane tempScrollPane = new JScrollPane(channels[chanAmount]);
        JPanel temp = new JPanel(new BorderLayout());
        temp.add(tempScrollPane);
        return temp;
    }
    
    public int activeTab() {
        return activeTab;
    }

    public String getActiveChannel() {
        return channelNames[activeTab];
    }

    public void sendLine(String input) {
        String nick = m.getCurrentNick();
        channels[activeTab].append(nick+": "+input+"\n");
    }
    
    public String getServer() {
        String s = (String)JOptionPane.showInputDialog(
                    frame,
                    "Enter server to connect to: ","Connect to Server", 1);
        
        return s;
    }
    
    public String getNick() {
        String n = (String)JOptionPane.showInputDialog(
                    frame,
                    "Enter nickname: ","Set Up Nickname", 1);
        
        return n; 
    }
    
    public String nickUsed() {
        String n = (String)JOptionPane.showInputDialog(
                    frame,
                    "Enter new nickname: ","Nickname already in use", 1);
        
        return n; 
    }
    
    public void addToServerMenu(String server) {
        buttonList[serverAmount] = new JRadioButtonMenuItem(server);
        buttonList[serverAmount].setSelected(true);
        serverGroup.add(buttonList[serverAmount]);
        Server.add(buttonList[serverAmount]);
        serverAmount++;
    }

    public String getTarget() {
        return (String)listModel.getElementAt(nickPanel.getSelectedIndex());
    }

    public void clearListModel() {
        System.out.println("Cleared listmodel");
        listModel.clear();
    }

    public void channelChange(int sel) {
        if (channelTopics[sel] != null ) {
            topic.setText(channelTopics[sel]);
        } else {
            topic.setText("");
        }
        changeUsers(m.getUsersChannel(channelNames[sel]));
    }
    
    public void updateUsers(int sel) {
        changeUsers(m.getUsersChannel(channelNames[sel]));
    }
    
    public String getMask() {
        String n = (String)JOptionPane.showInputDialog(
                    frame,
                    "Enter netmask","Enter netmask:", 1);
        
        return n; 
    }
    public String getJoinChannel() {
        String n = (String)JOptionPane.showInputDialog(
                    frame,
                    "Enter channel to join: ","Join new channel", 1);
        
        return n; 
    }

    public void setActiveTab(int sel) {
        activeTab=sel;
    }

    public void partChannel(String channelToPart) {
        System.out.println(channelToPart);
        for (int i = 0;i<=chanAmount;i++) {
            System.out.println(channelNames[i]);
            if ( channelNames[i].equals(channelToPart)) {
                chanList.removeTabAt(i);
            }
        }
        chanAmount--;
        if (chanAmount > 0) {
        for (int j = 1;j<channelNames.length-1;j++) {
         channelNames[j] = channelNames[j+1];
        }
        }
    }
    
    public int getSelectedServer() {
        int buttons = serverGroup.getButtonCount();
        int paluu = -1;
        for (int i = 0; i <= buttons; i++) {
            if (buttonList[i].isSelected()) {
                int selectedServer = i;
                paluu = i;
            }
        }
        
        return paluu;
    }

    public void setFocus() {
        input.requestFocusInWindow();
    }


}
