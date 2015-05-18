/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//checking commit
package com.kostenko.fadviser;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Pavel
 */
public class XMLParser {

    private Document doc = null;
    private final String OPENWEATHER_XML = "http://api.openweathermap.org/data/2.5/forecast/daily?q=Tbilisi&mode=xml&units=metric&cnt=7";
    private final String YANDEX_XML = "http://export.yandex.ru/weather-ng/forecasts/37549.xml";
    private final String WEATHERCOUA_XML = "http://xml.weather.co.ua/1.2/forecast/53137?dayf=5&userid=YourSite_com&lang=uk";
    private final String YAHOO_XML = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22Tbilisi%22)&format=xml&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

// The weather from BBC is not used. But link is noted for future implementation.
//    private final String BBC_XML = "http://open.live.bbc.co.uk/weather/feeds/en/611717/3dayforecast.rss";
    
    public Document getDocumentFromXML(String l) {
        try {
            URL forecastURL = new URL(l);
            HttpURLConnection conn = (HttpURLConnection) forecastURL.openConnection();
            InputSource source = new InputSource(conn.getInputStream());
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source);
        } catch (IOException|ParserConfigurationException | SAXException ex) {
            Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return doc;
    }

    public Weather parseDocumentOPENWEATHER() {
        
        Node n = getDocumentFromXML(OPENWEATHER_XML);
        
        float maxTempValue = -1000;
        int humid = 1000;
        
        NodeList rootNodeList = n.getChildNodes();
        Node rootNode = rootNodeList.item(0);
        NodeList subListLevel1 = rootNode.getChildNodes();
        for (int i = 0; i < subListLevel1.getLength(); i++) {
            Node subNodeLevel1 = subListLevel1.item(i);
            if ("forecast".equals(subNodeLevel1.getNodeName())) {
                NodeList subListLevel2 = subNodeLevel1.getChildNodes();
                for (int j = 0; j < subListLevel2.getLength(); j++) {
                    Node subNodeLevel2 = subListLevel2.item(j);
                    if ("time".equals(subNodeLevel2.getNodeName())) {
                        DateFormat formatOPENWEATHER = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        String s1 = getAttribute(subNodeLevel2,"day");
                        String s2 = formatOPENWEATHER.format(getTomorrow());
                        if (s1.equals(s2)){
                            NodeList subListLevel3 = subNodeLevel2.getChildNodes();
                            for (int k = 0; k < subListLevel3.getLength(); k++) {
                                Node subNodeLevel3 = subListLevel3.item(k);
                                if (subNodeLevel3.getNodeType() == Node.ELEMENT_NODE) {
                                    if ("temperature".equals(subNodeLevel3.getNodeName())){
                                        maxTempValue = Float.parseFloat(getAttribute(subNodeLevel3, "max"));
                                    } else if (subNodeLevel3.getNodeName().equals("humidity")) {
                                        humid = Integer.parseInt(getAttribute(subNodeLevel3, "value"));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Weather today = new Weather("OPENWEATHER",cal,maxTempValue,humid,"1dayforecast");
        return today;
    }

    public Weather parseDocumentYANDEX() {
        
        Node n = getDocumentFromXML(YANDEX_XML);
        
        float maxTempValue = -1000;
        float minTempValue = 1000;
        int dayHumid = -1000;
        int nightHumid = -1000;
        NodeList rootNodeList = n.getChildNodes();
        Node rootNode = rootNodeList.item(0);
        NodeList subListLevel1 = rootNode.getChildNodes();
        for (int i = 0; i < subListLevel1.getLength(); i++) {
            Node subNodeLevel1 = subListLevel1.item(i);
            if ("day".equals(subNodeLevel1.getNodeName())) {
                DateFormat formatYANDEX = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String s1 = getAttribute(subNodeLevel1,"date");
                String s2 = formatYANDEX.format(getTomorrow());
                if (s1.equals(s2)){
                    NodeList subListLevel2 = subNodeLevel1.getChildNodes();
                    for (int k = 0; k < subListLevel2.getLength(); k++) {
                        Node subNodeLevel2 = subListLevel2.item(k);
                        if (subNodeLevel2.getNodeType() == Node.ELEMENT_NODE) {
                            if ("day_part".equals(subNodeLevel2.getNodeName())){
                                if (getAttribute(subNodeLevel2,"type").equals("day_short")){
                                    NodeList subListLevel3 = subNodeLevel2.getChildNodes();
                                    for (int j = 0; j<subListLevel3.getLength(); j++){
                                        Node subNodeLevel3 = subListLevel3.item(j);
                                        if ("temperature".equals(subNodeLevel3.getNodeName())){
                                            maxTempValue = Float.parseFloat(subNodeLevel3.getFirstChild().getNodeValue());
                                        } else if ("humidity".equals(subNodeLevel3.getNodeName())){
                                            dayHumid = Integer.parseInt(subNodeLevel3.getFirstChild().getNodeValue());
                                        }
                                    }
                                } else if (getAttribute(subNodeLevel2,"type").equals("night_short")){
                                    NodeList subListLevel3 = subNodeLevel2.getChildNodes();
                                    for (int j = 0; j<subListLevel3.getLength(); j++){
                                        Node subNodeLevel3 = subListLevel3.item(j);
                                        if ("temperature".equals(subNodeLevel3.getNodeName())){
                                            minTempValue = Float.parseFloat(subNodeLevel3.getFirstChild().getNodeValue());
                                        } else if ("humidity".equals(subNodeLevel3.getNodeName())){
                                            nightHumid = Integer.parseInt(subNodeLevel3.getFirstChild().getNodeValue());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (minTempValue>maxTempValue){
            minTempValue+=maxTempValue;
            maxTempValue=minTempValue-maxTempValue;
        }
        int humid = dayHumid;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Weather today = new Weather("YANDEX",cal,maxTempValue,humid,"1dayforecast");
        return today;
    }

    public Weather parseDocumentWEATHERCOUA() {
        
        Node n = getDocumentFromXML(WEATHERCOUA_XML);
        
        float minTempValue = 1000;
        float maxTempValue = -1000;
        int minHumid = 1000;
        int maxHumid = -1000;
        NodeList rootNodeList = n.getChildNodes();
        Node rootNode = rootNodeList.item(0);
        NodeList subListLevel1 = rootNode.getChildNodes();
        for (int i = 0; i < subListLevel1.getLength(); i++) {
            Node subNodeLevel1 = subListLevel1.item(i);
            if ("forecast".equals(subNodeLevel1.getNodeName())) {
                NodeList subListLevel2 = subNodeLevel1.getChildNodes();
                for (int j = 0; j < subListLevel2.getLength(); j++) {
                    Node subNodeLevel2 = subListLevel2.item(j);
                    if ("day".equals(subNodeLevel2.getNodeName())) {
                        DateFormat formatOPENWEATHER = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        String s1 = getAttribute(subNodeLevel2,"date");
                        String s2 = formatOPENWEATHER.format(getTomorrow());
                        if (s1.equals(s2)){
                            NodeList subListLevel3 = subNodeLevel2.getChildNodes();
                            for (int k = 0; k < subListLevel3.getLength(); k++) {
                                Node subNodeLevel3 = subListLevel3.item(k);
                                if (subNodeLevel3.getNodeType() == Node.ELEMENT_NODE) {
                                    if ("t".equals(subNodeLevel3.getNodeName())){
                                        NodeList subListLevel4 = subNodeLevel3.getChildNodes();
                                        for (int l = 0; l<subListLevel4.getLength(); l++){
                                            Node subNodeLevel4 = subListLevel4.item(l);
                                            if ("min".equals(subNodeLevel4.getNodeName())){
                                                float localMin = Float.parseFloat(subNodeLevel4.getFirstChild().getNodeValue());
                                                if (minTempValue>localMin){
                                                    minTempValue=localMin;
                                                }
                                            } else if ("max".equals(subNodeLevel4.getNodeName())){
                                                float localMax = Float.parseFloat(subNodeLevel4.getFirstChild().getNodeValue());
                                                if (maxTempValue<localMax){
                                                    maxTempValue=localMax;
                                                }
                                            }
                                        }
                                    } else if ("hmid".equals(subNodeLevel3.getNodeName())){
                                        NodeList subListLevel4 = subNodeLevel3.getChildNodes();
                                        for (int l = 0; l<subListLevel4.getLength(); l++){
                                            Node subNodeLevel4 = subListLevel4.item(l);
                                            if ("min".equals(subNodeLevel4.getNodeName())){
                                                int localMinHumid = Integer.parseInt(subNodeLevel4.getFirstChild().getNodeValue());
                                                if (minHumid>localMinHumid){
                                                    minHumid=localMinHumid;
                                                }
                                            } else if ("max".equals(subNodeLevel4.getNodeName())){
                                                int localMaxHumid = Integer.parseInt(subNodeLevel4.getFirstChild().getNodeValue());
                                                if (maxHumid<localMaxHumid){
                                                    maxHumid=localMaxHumid;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        int humid = (minHumid + maxHumid)/2;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Weather today = new Weather("WEATHERCOUA",cal,maxTempValue,humid,"1dayforecast");
        return today;
    }

    public Weather parseDocumentYAHOO() {
        
        Node n = getDocumentFromXML(YAHOO_XML);
        
        float maxTempValue = -1000;
        int humid = 1000;
        
        NodeList rootNodeList = n.getChildNodes();
        Node rootNode = rootNodeList.item(0);
        NodeList subListLevel1 = rootNode.getChildNodes();
        for (int i = 0; i < subListLevel1.getLength(); i++) {
            Node subNodeLevel1 = subListLevel1.item(i);
            if ("results".equals(subNodeLevel1.getNodeName())) {
                NodeList subListLevel2 = subNodeLevel1.getChildNodes();
                for (int j = 0; j < subListLevel2.getLength(); j++) {
                    Node subNodeLevel2 = subListLevel2.item(j);
                    if ("channel".equals(subNodeLevel2.getNodeName())) {
                        NodeList subListLevel3 = subNodeLevel2.getChildNodes();
                        for (int k = 0; k < subListLevel3.getLength(); k++){
                            Node subNodeLevel3 = subListLevel3.item(k);
                            if ("item".equals(subNodeLevel3.getNodeName())){
                                NodeList subListLevel4 = subNodeLevel3.getChildNodes();
                                for (int l = 0; l < subListLevel4.getLength();l++){
                                    Node subNodeLevel4 = subListLevel4.item(l);
                                    if ("yweather:forecast".equals(subNodeLevel4.getNodeName())){
                                        if (getAttribute(subNodeLevel4,"date").equals(tomorrowsDateInStringForYahoo(getTomorrow()))){
                                            maxTempValue = Float.parseFloat(getAttribute(subNodeLevel4,"high"));
                                            maxTempValue = fahrenheitToCelsius(maxTempValue);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Weather today = new Weather("YAHOO",cal,maxTempValue,humid,"1dayforecast");
        return today;
    }
    
    public Weather actualWeatherFromYAHOO() {
        String link = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22Tbilisi%22)&format=xml&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
        Document n = getDocumentFromXML(link);
        float actualMaxTemp = -1000;
        int actualHumid = 1000;
        
        NodeList rootNodeList = n.getChildNodes();
        Node rootNode = rootNodeList.item(0);
        NodeList subListLevel1 = rootNode.getChildNodes();
        for (int i = 0; i < subListLevel1.getLength(); i++) {
            Node subNodeLevel1 = subListLevel1.item(i);
            if ("results".equals(subNodeLevel1.getNodeName())) {
                NodeList subListLevel2 = subNodeLevel1.getChildNodes();
                for (int j = 0; j < subListLevel2.getLength(); j++) {
                    Node subNodeLevel2 = subListLevel2.item(j);
                    if ("channel".equals(subNodeLevel2.getNodeName())) {
                        NodeList subListLevel3 = subNodeLevel2.getChildNodes();
                        for (int k = 0; k < subListLevel3.getLength(); k++){
                            Node subNodeLevel3 = subListLevel3.item(k);
                            if ("item".equals(subNodeLevel3.getNodeName())){
                                NodeList subListLevel4 = subNodeLevel3.getChildNodes();
                                for (int l = 0; l < subListLevel4.getLength();l++){
                                    Node subNodeLevel4 = subListLevel4.item(l);
                                    if ("yweather:condition".equals(subNodeLevel4.getNodeName())){
                                        actualMaxTemp = Float.parseFloat(getAttribute(subNodeLevel4,"temp"));
                                        actualMaxTemp = fahrenheitToCelsius(actualMaxTemp);
                                    }
                                }
                            } else if ("yweather:atmosphere".equals(subNodeLevel3.getNodeName())){
                                actualHumid = Integer.parseInt(getAttribute(subNodeLevel3,"humidity"));
                            }
                        }
                    }
                }
            }
        }
        Weather today = new Weather("YAHOO",Calendar.getInstance(),actualMaxTemp,actualHumid,"actual");
        return today;
    }
    
    private String getAttribute(Node x, String attribute) {
        return x.getAttributes().getNamedItem(attribute).getNodeValue();
    }

    public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc),
                new StreamResult(new OutputStreamWriter(out, "UTF-8")));
    }

    private Date getTomorrow() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        
        return tomorrow;
    }
    
    static private Date stringToDate(String s){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(s);
        } catch (ParseException ex) {
            Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return date;
    }
    
    private String tomorrowsDateInStringForYahoo (Date d){
        DateFormat dF = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
        return dF.format(d);
    }
    
    private float fahrenheitToCelsius (float c){
        return (c-32)*5/9.0f;
    }
    

}
