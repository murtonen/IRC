/*
 * Olio jonka avulla voidaan serializee ohjelman preferenssit
 * Toteuttaa getterit ja setterit preferenssien arvoille
 */

package irc;

import java.io.Serializable;

/**
 *
 * @author Ville Murtonen
 */

public class Preferences implements Serializable {
    // Muuttujat
    private String nick;
    private String alt;
    private String user;
    private String real;
    
    // Rakentaja
    public Preferences() {
    }
    
    // Getterit ja Setterit
    public String getNick() {
        return nick;
    }
    
    public String getAlt() {
        return alt;
    }
    
    public String getUser() {
        return user;
    }
    
    public String getReal() {
        return real;
    }
    
    public void setNick(String n) {
        nick = n;
    }
    
    public void setAlt(String a) {
        alt = a;
    }
    
    public void setUser(String u) {
        user = u;
    }
    
    public void setReal(String r) {
        real = r;
    }
}
