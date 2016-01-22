package pl.edu.uksw.prir.messenger;

import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Jakub on 19/01/16.
 */
public class Roster {

    private static String LOG_ROSTER = "ROSTER: ";
    private static String FILENAME = "roster.xml";
    public List<Friend> friendList = new ArrayList<Friend>();
    private Context context;
    String to;
    String type;
    String query;

    Roster(String from, String type, String query, Context context){
        this.to = from;
        this.type = type;
        this.query = query;
        this.context = context;
    }

    public void initRoster() throws FileNotFoundException {

        StringBuffer xmlStr = new StringBuffer();
        xmlStr.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"+
                "<iq \n"+
                "to= \""+ this.to+"\"\n"+
                "type= \""+ this.type+"\"\n"+
                ">\n"+
                "<query xmlns=\""+ this.query+"\">\n");
        if (!friendList.isEmpty()){
            for (int i = 0; i < friendList.size(); i++){
                xmlStr.append("<item jid=\"" + friendList.get(i).getJid() + "\"\n"+
                        "name = \"" + friendList.get(i).getName() + "\"\n"+
                        "subscription= \"" + friendList.get(i).getSubscription() + "\"\n"+
                        "/>\n");
            }
        }
        xmlStr.append(
                "</query> \n"+
                "</iq>");

        Document doc = convertStringToDocument(xmlStr.toString());

        String str = convertDocumentToString(doc);
        writeXmlToFile(str);
        //System.out.println(str);
    }

    private static String convertDocumentToString(Document doc) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString();
            return output;
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try
        {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void writeXmlToFile(String data){
//        PrintWriter out = new PrintWriter("roster.xml");
//        out.println(data);
//        out.close();
        try{
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
        }
        catch (IOException ioe) {
            Log.e(LOG_ROSTER, "Write file failed");
        }
    }

    private String openXmlFromFile() throws FileNotFoundException {
        ArrayList<String> lines = new ArrayList<String>();
        String data = null;
//        BufferedReader bufferedReader = new BufferedReader(new FileReader(NAME));
//        data = new Scanner(new File(NAME)).useDelimiter("\\Z").next();
        try{
            FileInputStream fis = context.openFileInput(FILENAME);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));

            String line = bufferedReader.readLine();
            while(line != null){
                lines.add(line+"\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            fis.close();
        } catch (Exception e){
            Log.e(LOG_ROSTER, "Read file failed");
        }
        data = lines.toString();
        return data;
    }

    public void parseRosterXml(){
        String input = null;
        try {
            input = openXmlFromFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (!input.equals(null)) {
            InputStream inputStream = null;
            try {
                inputStream = new ByteArrayInputStream(input.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
                XmlPullParser rosterParser = xmlFactoryObject.newPullParser();
                rosterParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                rosterParser.setInput(inputStream, null);
                parseXmlAndSetValues(rosterParser);
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            Log.d(LOG_ROSTER,"Empty roster.xml file");
        }
    }

    private void parseXmlAndSetValues(XmlPullParser msgParser){
        int event;
        int i = 0;
        String text = null;
        String name;

        try{
            event = msgParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT){
                name = msgParser.getName();

                switch (event){
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("item")){
                            friendList.get(i).setJid(msgParser.getAttributeValue(null, "jid"));
                            friendList.get(i).setName(msgParser.getAttributeValue(null, "name"));
                            friendList.get(i).setSubscription(msgParser.getAttributeValue(null, "subscription"));
                            i++;
                        }
                        break;
                }
                event = msgParser.next();
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Friend> getRoster(){
        if(type.equals("get") && query.equals("jabber:ig:roster")){
            parseRosterXml();
        }
        return friendList;
    }

    public void addFriend(String jid, String name, String sub){
        Friend buddy = new Friend(jid, name, sub);
        friendList.add(buddy);
    }
}
