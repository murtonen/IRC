/*
 * Server olio sisaltaa ko. palvelimen kanavat
 * 
 */
package irc;

import java.util.ArrayList;

/**
 *
 * @author Ville Murtonen
 */
public class Server {
    
    // Arraylisti kanaville
    ArrayList channels;
        
    public Server() {
        channels = new ArrayList();
    }
    
    // Kanavan lisays listaan
    public void addChannel(int i) {
        channels.add(i);
    }
    
    // Kanavan poisto listasta
    public void removeChannel(int i) {
        if (!channels.isEmpty()) {
        channels.remove((Object)i);
        }
    }
    
    // Listan palautus
    public ArrayList Channels() {
        return channels;
    }
}
