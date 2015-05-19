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
public class XMLParser {

    private Document doc = null;
    private final String OPENWEATHER_XML = "http://api.openweathermap.org/data/2.5/forecast/daily?q=Tbilisi&mode=xml&units=metric&cnt=7";
    private final String YANDEX_XML = "http://export.yandex.ru/weather-ng/forecasts/37549.xml";
    private final String WEATHERCOUA_XML = "http://xml.weather.co.ua/1.2/forecast/53137?dayf=5&userid=YourSite_com&lang=uk";
    private final String YAHOO_XML = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22Tbilisi%22)&format=xml&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

// The weather from BBC is not used. But link is noted for future implementation.
//    private final String BBC_XML = "http://open.live.bbc.co.uk/weather/feeds/en/611717/3dayforecast.rss";
    public Document getDocumentFromXML(String l) {
        try {
            URL forecastURL = new URL(l);
            HttpURLConnection conn = (HttpURLConnection) forecastURL.openConnection();
            InputSource source = new InputSource(conn.getInputStream());
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source);
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return doc;
    }

    public Weather parseDocumentOPENWEATHER() {

        Node n = getDocumentFromXML(OPENWEATHER_XML);

        float maxTempValue = -1000;
        int humid = 1000;

        Node weatherdata = getSubnode(n, "weatherdata");
        Node forecast = getSubnode(weatherdata, "forecast");
        Node time = getSubnodes(forecast, "time")
                .stream()
                .filter(node -> getAttribute(node, "day")
                        .equals(SIMPLE_DATE_FORMAT.format(getTomorrow())))
                .findAny().get();

        Node temperature = getSubnode(time, "temperature");
        maxTempValue = Float.parseFloat(getAttribute(temperature, "max"));

        Node humidity = getSubnode(time, "humidity");
        humid = Integer.parseInt(getAttribute(humidity, "value"));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Weather today = new Weather("OPENWEATHER", cal, maxTempValue, humid, "1dayforecast");
        return today;
    }

    private Node getSubnode(Node parentNode, String subnodeName) {
        Node resultNode = null;
        NodeList subList = parentNode.getChildNodes();
        for (int i = 0; i < subList.getLength(); i++) {
            Node subNode = subList.item(i);
            if (subnodeName.equals(subNode.getNodeName())) {
                resultNode = subNode;
            }
        }
        return resultNode;
    }

    private List<Node> getSubnodes(Node node, String name) {
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

    ;

    public Weather parseDocumentYANDEX() {

        Node n = getDocumentFromXML(YANDEX_XML);

        float maxTempValue = -1000;
        float minTempValue = 1000;
        int dayHumid = -1000;
        int nightHumid = -1000;

        Node forecast = getSubnode(n, "forecast");

        Node day = getSubnodes(forecast, "day")
                .stream()
                .filter(node -> getAttribute(node, "date")
                        .equals(SIMPLE_DATE_FORMAT.format(getTomorrow())))
                .findAny().get();

        Node daypartDay = getSubnodes(day, "day_part")
                .stream()
                .filter(node -> getAttribute(node, "type")
                        .equals("day_short"))
                .findAny().get();

        Node temperatureDayshort = getSubnode(daypartDay, "temperature");
        maxTempValue = Float.parseFloat(temperatureDayshort.getFirstChild().getNodeValue());

        Node humidityDayshort = getSubnode(daypartDay, "humidity");
        dayHumid = Integer.parseInt(humidityDayshort.getFirstChild().getNodeValue());

        Node daypartNight = getSubnodes(day, "day_part")
                .stream()
                .filter(node -> getAttribute(node, "type")
                        .equals("night_short"))
                .findAny().get();

        Node temperatureNightshort = getSubnode(daypartNight, "temperature");
        minTempValue = Float.parseFloat(temperatureNightshort.getFirstChild().getNodeValue());

        Node humidityNightshort = getSubnode(daypartNight, "humidity");
        nightHumid = Integer.parseInt(humidityNightshort.getFirstChild().getNodeValue());

        if (minTempValue > maxTempValue) {
            minTempValue += maxTempValue;
            maxTempValue = minTempValue - maxTempValue;
        }
        int humid = dayHumid;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Weather today = new Weather("YANDEX", cal, maxTempValue, humid, "1dayforecast");
        return today;
    }

    public Weather parseDocumentWEATHERCOUA() {

        Node n = getDocumentFromXML(WEATHERCOUA_XML);

        float maxTempValue = -1000;
        int minHumid = 1000;
        int maxHumid = -1000;

        Node forecastRoot = getSubnode(n, "forecast");

        Node forecastSub = getSubnode(forecastRoot, "forecast");

        List<Node> dayList = getSubnodes(forecastSub, "day")
                .stream()
                .filter(node -> getAttribute(node, "date")
                        .equals(SIMPLE_DATE_FORMAT.format(getTomorrow())))
                .collect(Collectors.toList());

        for (Node day : dayList) {
            Node t = getSubnode(day, "t");

            Node max = getSubnode(t, "max");
            float localMax = Float.parseFloat(max.getFirstChild().getNodeValue());
            if (maxTempValue < localMax) {
                maxTempValue = localMax;
            }

            Node hmid = getSubnode(day, "hmid");

            Node minH = getSubnode(hmid, "min");
            int localMinHumid = Integer.parseInt(minH.getFirstChild().getNodeValue());
            if (minHumid > localMinHumid) {
                minHumid = localMinHumid;
            }

            Node maxH = getSubnode(hmid, "max");
            int localMaxHumid = Integer.parseInt(maxH.getFirstChild().getNodeValue());
            if (maxHumid < localMaxHumid) {
                maxHumid = localMaxHumid;
            }
        }
        int humid = (minHumid + maxHumid) / 2;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Weather today = new Weather("WEATHERCOUA", cal, maxTempValue, humid, "1dayforecast");
        return today;
    }

    public Weather parseDocumentYAHOO() {

        Node n = getDocumentFromXML(YAHOO_XML);

        float maxTempValue = -1000;
        int humid = 1000;

        Node query = getSubnode(n, "query");

        Node results = getSubnode(query, "results");

        Node channel = getSubnode(results, "channel");

        Node item = getSubnode(channel, "item");

        Node yweather = getSubnodes(item, "yweather:forecast")
                .stream()
                .filter(node -> getAttribute(node, "date")
                        .equals(tomorrowsDateInStringForYahoo(getTomorrow())))
                .findAny().get();

        maxTempValue = Float.parseFloat(getAttribute(yweather, "high"));
        maxTempValue = fahrenheitToCelsius(maxTempValue);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Weather today = new Weather("YAHOO", cal, maxTempValue, humid, "1dayforecast");
        return today;
    }

    public Weather actualWeatherFromYAHOO() {
        String link = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22Tbilisi%22)&format=xml&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
        Document n = getDocumentFromXML(link);
        float actualMaxTemp = -1000;
        int actualHumid = 1000;

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

    private String getAttribute(Node x, String attribute) {
        String str = "-1000";
        try {
            str = x.getAttributes().getNamedItem(attribute).getNodeValue();
        } catch (NullPointerException e) {
            System.out.println("There are no attribute: " + attribute + " in the node: " + x.getNodeName());
        } finally {
            return str;
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

    private Date getTomorrow() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

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
            Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return date;
    }

    private String tomorrowsDateInStringForYahoo(Date d) {
        DateFormat dF = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
        return dF.format(d);
    }

    private float fahrenheitToCelsius(float c) {
        return (c - 32) * 5 / 9.0f;
    }

}
