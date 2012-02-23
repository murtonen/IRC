/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irc;

import org.jibble.pircbot.User;

/**
 *
 * @author Ville Murtonen
 */
public class IRCController {
    IRCModel model;
    IRCView view;
    
    public IRCController (IRCModel m, IRCView v) {
        model = m;
        view = v;
    }
    
    public void getUserList() {
        //return model.getUserList();
    }
    
}
