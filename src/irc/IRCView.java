/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import org.jibble.pircbot.User;

/**
 *
 * @author Ville Murtonen
 */
public class IRCView extends JPanel implements ActionListener,MouseListener,Observer {
    
        // Paalayoutti
        JTextField topic,input;
        JTextArea chat;
        JTabbedPane chanList;
        JFrame frame;
        
        // nickList
        DefaultListModel listModel;
        JList nickPanel;
        
        // JmenuBar
        JMenuBar menuBar;
        JMenu File;
        JMenuItem Open;
        JMenuItem Save;
        
        // Panels
        JPanel northPanel;
        JPanel eastPanel;
        JPanel southPanel;
        JPanel defaultti;
        
        // PopUpMenu
        JPopupMenu popup;
        JMenuItem kick,ban,op,voice;
        
        // Model
        IRCModel m;
        
    public IRCView(IRCModel model) {
        
        // Framen luominen
        frame = new JFrame("(A)wesome irc (C)lient (E)xperience");
        frame.setSize(400,125);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
        
        // Asetetaan observer
        m = model;
        m.addObserver(this);
        
        // JTextFieldit
        topic = new JTextField(40);
        input = new JTextField(40);
        topic.setEditable(false);
        
        // Nick listing
        listModel = new DefaultListModel();
        nickPanel = new JList(listModel);
        nickPanel.setBackground(topic.getBackground());
        nickPanel.addMouseListener(this);
        
        // JTextArea
        chat = new JTextArea(30,30);
        
        // JPanelit
        northPanel = new JPanel(new BorderLayout());
        eastPanel = new JPanel(new BorderLayout());
        eastPanel.setPreferredSize(new Dimension(200,100));
        southPanel = new JPanel(new BorderLayout());
        
        // JTabbedPane
        chanList = new JTabbedPane();
        defaultti = new JPanel(new BorderLayout());
        defaultti.add(chat);
        chanList.addTab("Default",defaultti);
        
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
        eastPanel.add(nickPanel);
        southPanel.add(input);
        
        // Luodaan Menubar
        menuBar = new JMenuBar();
        
        // Asetetaan siihen otsikot
        File = new JMenu("File");
        menuBar.add(File);
 
        // Ja niihin sisalto
        Open = new JMenuItem("Open");
        Save = new JMenuItem("Save");
        // Ja listenerit
        Open.addActionListener(this);
        Save.addActionListener(this);
        // Ja laitetaan menuun
        File.add(Open);
        File.add(Save);
        
        // PopUpMenu
        popup = new JPopupMenu();
        kick = new JMenuItem("Kick");
        kick.addActionListener(this);
        popup.add(kick);
        
        // Laitetaan frameen Jpanelit
        frame.setJMenuBar(menuBar);
        frame.add(northPanel,BorderLayout.NORTH);
        frame.add(eastPanel,BorderLayout.EAST);
        frame.add(southPanel,BorderLayout.SOUTH);
        frame.add(chanList,BorderLayout.CENTER);
        
        // Naytetaan sisalto
        frame.pack();
        frame.setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        
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
    
    public void addText(String text) {
        chat.append(text+"\n");
    }
    
    public void update(Observable o, Object arg) {
        String msg = (String)arg;
        if ( msg.equals("newText")) {
            addLine(m.getLine());
        } else if (msg.equals("userChange")) {
            changeUsers(m.getUsers());
        }
        System.out.println("Got updated with: "+msg);
    }
    
    public void addLine(String rivi) {
        chat.append(rivi+"\n");
    }
    
    public void changeUsers(User[] users) {
        int koko = users.length;
        for (int i = 0;i<koko;i++) {
            addNick(users[i].getNick());
        }
    }
    
    public void addNick(String nick) {
        System.out.println("Lisataan nick!");
        listModel.addElement((String)nick);
    }
}
