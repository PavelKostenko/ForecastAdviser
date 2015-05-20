/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//checking commit
package com.kostenko.parsers;

import com.kostenko.db.Weather;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
public abstract class WeatherParser {

    protected final String OPENWEATHER_XML = "http://api.openweathermap.org/data/2.5/forecast/daily?q=Tbilisi&mode=xml&units=metric&cnt=7";
    protected final String YANDEX_XML = "http://export.yandex.ru/weather-ng/forecasts/37549.xml";
    protected final String WEATHERCOUA_XML = "http://xml.weather.co.ua/1.2/forecast/53137?dayf=5&userid=YourSite_com&lang=uk";
    protected final String YAHOO_XML = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22Tbilisi%22)&format=xml&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
    protected static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    protected static final int MAX_INIT_TEMP = -1000;
    protected static final int MIN_INIT_TEMP = 1000;
    protected static final int MAX_INIT_HUMID = -1000;
    protected static final int MIN_INIT_HUMID = 1000;

// The weather from BBC is not used. But link is noted for future implementation.
//    private final String BBC_XML = "http://open.live.bbc.co.uk/weather/feeds/en/611717/3dayforecast.rss";
  
    public abstract Weather getFutureWeather();
    
    public static Document getDocumentFromXML(String l) {
        Document doc = null;
        try {
            URL forecastURL = new URL(l);
            HttpURLConnection conn = (HttpURLConnection) forecastURL.openConnection();
            InputSource source = new InputSource(conn.getInputStream());
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source);
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            Logger.getLogger(WeatherParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return doc;
    }
    
    public static Weather getActualWeather() {
        String link = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22Tbilisi%22)&format=xml&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
        Document n = getDocumentFromXML(link);
        float actualMaxTemp = MAX_INIT_TEMP;
        int actualHumid = MAX_INIT_HUMID;

        Node query = getSubnode(n, "query");

        Node results = getSubnode(query, "results");

        Node channel = getSubnode(results, "channel");

        Node yweatherAtmosphere = getSubnode(channel, "yweather:atmosphere");
        actualHumid = Integer.parseInt(getAttribute(yweatherAtmosphere, "humidity"));

        Node item = getSubnode(channel, "item");

        Node yweatherCondition = getSubnode(item, "yweather:condition");

        actualMaxTemp = Float.parseFloat(getAttribute(yweatherCondition, "temp"));
        actualMaxTemp = fahrenheitToCelsius(actualMaxTemp);

        Weather today = new Weather("YAHOO", Calendar.getInstance(), actualMaxTemp, actualHumid, "actual");
        return today;
    }

    protected static Node getSubnode(Node parentNode, String subnodeName) {
        NodeList subList = parentNode.getChildNodes();
        for (int i = 0; i < subList.getLength(); i++) {
            Node subNode = subList.item(i);
            if (subnodeName.equals(subNode.getNodeName())) {
                return subNode;
            }
        }
        throw new RuntimeException("Node not found");
    }

    protected static List<Node> getSubnodes(Node node, String name) {
        NodeList nodeList = node.getChildNodes();
        List<Node> result = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node currentNode = nodeList.item(i);
            if (name.equals(currentNode.getNodeName())) {
                result.add(currentNode);
            }
        }
        return result;
    }

    protected static String getAttribute(Node x, String attribute) {
        try {
            return x.getAttributes().getNamedItem(attribute).getNodeValue();
        } catch (NullPointerException e) {
            System.out.println(String.format("There are no attribute: %s in the node: %s",
                    attribute,
                    x.getNodeName()));
            return "-1000";
        }
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

    protected Date getTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        return tomorrow;
    }

    static private Date stringToDate(String s) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(s);
        } catch (ParseException ex) {
            Logger.getLogger(WeatherParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return date;
    }

    protected String tomorrowsDateInStringForYahoo(Date d) {
        DateFormat dF = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
        return dF.format(d);
    }

    protected static float fahrenheitToCelsius(float c) {
        return (c - 32) * 5 / 9.0f;
    }

}
