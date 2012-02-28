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
        
        // Kick Button Listener
        v.setKickListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String nick = v.getTarget();
                m.kickTarget(nick);
            }
        });
        
        // Join Button Listener
        v.setJoinButtonListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String channelToJoin;
                channelToJoin = v.getJoinChannel();
                v.joinChannel(channelToJoin);
                m.joinChannel(channelToJoin);
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
                if ( input.startsWith("/join")) {
                    splitted = input.split(" ");
                    v.joinChannel(splitted[1]);
                    m.joinChannel(splitted[1]);
                    
                } else {
                    channel = v.getActiveChannel();
                    m.sendLine(channel,input);
                    v.sendLine(input);
                }
                
            }
        });
        
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
                    } catch (IrcException ex) {
                        Logger.getLogger(IRCController.class.getName()).log(Level.SEVERE, null, ex);
                    }
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
