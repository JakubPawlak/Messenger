package pl.edu.uksw.prir.messenger;

/**
 *
 * @author Wojciech Pokora
 * @author Jakub Pawlak
 * @author Patryk Szewczyk
 * @author Katarzyna Wiater
 * @author Agnieszka Musiał
 * @author Michał Darkowski
 *
 */


public class Friend {
    private String jid;
    private String name;
    private String subscription;

    Friend(String jid, String name, String subscription){
        this.jid = jid;
        this.name = name;
        this.subscription = subscription;
    }

    public void setJid(String jid){ this.jid = jid; }
    public void setName(String name){ this.name = name; }
    public void setSubscription(String subscription) { this.subscription = subscription; }

    public String getJid(){ return jid; }
    public String getName() { return name; }
    public String getSubscription() { return subscription; }
}
