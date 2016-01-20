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
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
 
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;


public class Message {
    private String body;
    private String from;
    private String id;
    private String to;
    // private String type;
    // private Date date;
    
    Message(){
        this.from = null;
        this.id = null;
        this.to = null;
        this.body = null;
    }
    
    Message(String  msg_body, String msg_from, String msg_id, String msg_to){
        this.body = msg_body;
        this.from = msg_from;
        this.id = msg_id;
        this.to = msg_to;
    }

    public String getBody(){ return body; }

    public String getFrom(){ return from; }

    public String getId() { return id; }

    public String getTo() { return to; }

    private void setBody(String body) { this.body = body; }

    private void setFrom(String from) { this.from = from; }

    private void setId(String id) { this.id = id; }

    private void setTo(String to) { this.id = id; }
    
    public void messageString() throws FileNotFoundException{
        final String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"+
                                "<message \n"+
                                "from= \""+ this.from+"\"\n"+
                                "id= \""+ this.id+"\"\n"+
                                "to= \""+ this.to+"\"\n"+
                                ">\n"+
                                "<body> \n"+this.body+"\n"+"</body> \n"+
                                "</message>";
        
        Document doc = convertStringToDocument(xmlStr);
         
        String str = convertDocumentToString(doc);
        writeMessageToFile(str);
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
    
    public void writeMessageToFile(String data) throws FileNotFoundException{
        PrintWriter out = new PrintWriter("message.xml");
        out.println(data);
        out.close();
    }

    public String openMessageFromFile() throws FileNotFoundException{
        String data = null;
        BufferedReader bufferedReader = new BufferedReader(new FileReader("message.xml"));
        data = new Scanner(new File("message.xml")).useDelimiter("\\Z").next();
        return data;
    }

    // parsuje xml podany jako string ustawiajac odpowiednie zmienne
    public void parseXml (String input){
        InputStream inputStream = null;
        // tworzy strumien wejscia z wczytanego pliku xml w zmiennej String
        try {
            inputStream = new ByteArrayInputStream(input.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        try {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser messageParser = xmlFactoryObject.newPullParser();
            messageParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            messageParser.setInput(inputStream, null);
            parseXmlAndSetValues(messageParser);
            inputStream.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void parseXmlAndSetValues(XmlPullParser msgParser){
        int event;
        String text = null;
        String name;

        try{
            event = msgParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT){
                name = msgParser.getName();

                switch (event){
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        text = msgParser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("body")){
                            setBody(text);
                        }
                        else if (name.equals("message")){
                            setFrom(msgParser.getAttributeValue(null, "from"));
                            setId(msgParser.getAttributeValue(null, "id"));
                            setTo(msgParser.getAttributeValue(null, "to"));
                        }
                        break;
                }
                event = msgParser.next();
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
