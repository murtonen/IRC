/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jibble.pircbot.IrcException;

/**
 *
 * @author Ville Murtonen
 */
public class IRCController implements ActionListener {
    IRCModel model;
    IRCView view;
    
    public IRCController (final IRCModel m, final IRCView v) {
        model = m;
        view = v;
        v.setJoinButtonListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String nick = v.getNick();
                String server = v.getServer();
                if (server != null && !server.equals("")) {
                    try {
                        m.connectToServer(server,nick);
                    } catch (IrcException ex) {
                        Logger.getLogger(IRCController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }  
            }
        });
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
    
}
