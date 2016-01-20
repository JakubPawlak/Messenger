package pl.edu.uksw.prir.messenger;

import org.junit.Test;

import java.io.FileNotFoundException;

/**
 * Created by Jakub on 20/01/16.
 */
public class RosterXmlTest {
    @Test
    public void testRoster() {
        Roster roster = new Roster("Romeo", "result", "jabber:iq:roster'");
        roster.addFriend("romeo@gmail.com", "romeo", "both");
        roster.addFriend("juliette@gmail.com", "juliette", "both");
        try {
            roster.initRoster();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


}
