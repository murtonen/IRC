/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jibble.pircbot.IrcException;

/**
 *
 * @author Ville Murtonen
 */
public class IRCController implements ActionListener,ChangeListener {
    IRCModel model;
    IRCView view;
    
    public IRCController (final IRCModel m, final IRCView v) {
        model = m;
        view = v;
        
        // Connect Listener
        v.setConnectListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String nick = v.getNick();
                String server = v.getServer();
                if (server != null && !server.equals("")) {
                    try {
                        m.connectToServer(server,nick);
                        v.addToServerMenu(server);
                        v.setFocus();
                    } catch (IrcException ex) {
                        Logger.getLogger(IRCController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }  
            }
        });
        
        // Connect Listener
        v.setDisconnectListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                m.disconnectServer(v.getSelectedServer());
                m.updateActiveServer();
                
            }
        });
        
        // chanListListener
         v.setChanListListener(new ChangeListener() {
            @Override
            public void  stateChanged(ChangeEvent e) {
                // Get current tab
                int sel = v.getSelectedPaneIndex();
                v.setActiveTab(sel);
                v.clearListModel();   
                v.channelChange(sel);
                
            }
            });
        
        // Join Button Listener
        v.setJoinButtonListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String channelToJoin;
                int index;
                channelToJoin = v.getJoinChannel();
                index = v.joinChannel(channelToJoin);
                m.joinChannel(channelToJoin,index);
                v.setFocus();
            }
        });
             
        // Part Button Listener
        v.setPartButtonListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String channelToPart;
                channelToPart = m.getActiveChannel();
                v.partChannel(channelToPart);
                m.partChannel(channelToPart);
            }
        });
        
        // Kick Button Listener
        v.setKickListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                String nick = v.getTarget();
                m.kickTarget(nick);
                m.updateUserList();
            }
        });
        
        // Op/Deop Button Listener
        v.setOpListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                String nick = v.getTarget();
                m.opOrDeop(nick);
            }
        });
        
        // Voice/DeVoice Button Listener
        v.setVoiceListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                String nick = v.getTarget();
                m.opOrDeop(nick);
            }
        });
        
        // Ban Button Listener
        v.setBanListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                String mask = v.getMask();
                m.ban(mask);
            }
        });
        
        // UnBan Button Listener
        v.setUnBanListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                String mask = v.getMask();
                m.unban(mask);
            }
        });
        
        // Input Area Listener
        v.setInputAreaListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String input="";
                String channel="";
                String[] splitted;
                input = v.getInput();    
                int index;
                if ( input.startsWith("/join")) {
                    splitted = input.split(" ");
                    index = v.joinChannel(splitted[1]);
                    m.joinChannel(splitted[1], index);
                    
                } else {
                    channel = v.getActiveChannel();
                    m.sendLine(channel,input);
                    v.sendLine(input);
                }
                
            }
        });
        
        
    }
    
    public void getUserList() {
        //return model.getUserList();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
