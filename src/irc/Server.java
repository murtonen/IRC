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
        int index = i;
        channels.add(index);
    }
    
    // Kanavan poisto listasta
    public void removeChannel(int i) {
        int index = i;
        channels.remove(index);
    }
    
    // Listan palautus
    public ArrayList Channels() {
        return channels;
    }
}
