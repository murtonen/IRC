/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package irc;

import java.util.ArrayList;

/**
 *
 * @author Ville Murtonen
 */
public class Server {
    ArrayList channels;
        
    public Server() {
        channels = new ArrayList();
    }
    
    public void addChannel(int i) {
        int index = i;
        channels.add(index);
    }
    
    public ArrayList Channels() {
        return channels;
    }
}
