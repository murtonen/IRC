/*
 * Controller luokassa toteutetaan ohjelman "aly", eli viestien valitys
 * ja niiden kasittely viewin ja modelin valilla
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
    // Maaritellaan kayttoon Model ja View
    IRCModel model;
    IRCView view;
    
    public IRCController (final IRCModel m, final IRCView v) {
        // Asetetaan paaohjelmasta saadut model ja view paikalleen
        model = m;
        view = v;
        
        // Connectorit on toteutettu inject tyyppisesti, eli view toteuttaa metodit
        // listenerin lisaamiseksi ja controllerissa ne asetetaan, jotta actionperformed saadaan tanne
        
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
                        v.setServerItemListener(new ActionListener() {
                        
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                m.setActiveServer(v.getIndex());
                            }
                        });
                        v.setFocus();
                    } catch (IrcException ex) {
                        Logger.getLogger(IRCController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }  
            }
        });
        
        // DiscConnect Listener
        v.setDisconnectListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (m.isConnected()) {
                m.disconnectServer(v.getSelectedServer());
                m.updateActiveServer();
                } else {
                    v.cannotDisconnect();
                }
                
            }
        });
        
        // ReConnect Listener
        v.setReconnectListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (m.canReconnect()) {
                    try {
                        m.reconnect();
                    } catch (IrcException ex) {
                        Logger.getLogger(IRCController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    v.cannotReconnect();
                }
                
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
         
        // Connect Listener
        v.setPreferencesListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                v.getPreferences();
            }
        });
        
        // Join Button Listener
        v.setJoinButtonListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (m.isConnected()) {
                String channelToJoin;
                int index;
                channelToJoin = v.getJoinChannel();
                index = v.joinChannel(channelToJoin);
                m.joinChannel(channelToJoin,index);
                v.setFocus();
                } else {
                    v.noServer();
                }
            }
        });
             
        // Part Button Listener
        v.setPartButtonListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (m.isConnected()) {
                    int indexPart = v.partChannelIndex();
                    m.partChannel(v.partChannel(),indexPart);
                } else {
                    v.noServer();
                }
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
                m.voiceOrDevoice(nick);
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
        
        // UnBan Button Listener
        v.setQuitListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                m.quit();
                } catch (Exception ex) {
                    
                }
                try {
                v.quit();
                } catch (Exception ex) {
                    
                }
                System.exit(0);
            }
        });
    }
    
    // Should not be used! Debugging. (Yes, left it here.)
    public void getUserList() {
        //return model.getUserList();
    }

    // No need but netbeans wanted these
    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // No need but netbeans wanted these
    @Override
    public void stateChanged(ChangeEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
