/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kostenko.fchooser;

import org.w3c.dom.Document;

/**
 *
 * @author Pavel
 */
public class ForecastChooser {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        XMLParser parser = new XMLParser();
        DBConnector dBc = new DBConnector();

        String link1 = "http://api.openweathermap.org/data/2.5/forecast/daily?q=Tbilisi&mode=xml&units=metric&cnt=7";
        String link2 = "http://export.yandex.ru/weather-ng/forecasts/37549.xml";
        String link3 = "http://xml.weather.co.ua/1.2/forecast/53137?dayf=5&userid=YourSite_com&lang=uk";
        String link4 = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22Tbilisi%22)&format=xml&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
        String link5 = "http://open.live.bbc.co.uk/weather/feeds/en/611717/3dayforecast.rss";
        
        Document d1 = parser.getDocumentFromXML(link1);
        Document d2 = parser.getDocumentFromXML(link2);
        Document d3 = parser.getDocumentFromXML(link3);
        Document d4 = parser.getDocumentFromXML(link4);
        
        Weather w1 = parser.parseDocumentOPENWEATHER(d1);
        Weather w2 = parser.parseDocumentYANDEX(d2);
        Weather w3 = parser.parseDocumentWEATHERCOUA(d3);
        Weather w4 = parser.parseDocumentYAHOO(d4);
        Weather actualWeather = parser.actualWeatherFromYAHOO();
        
        dBc.writeToDB(w1);
        dBc.writeToDB(w2);
        dBc.writeToDB(w3);
        dBc.writeToDB(w4);
        dBc.writeToDB(actualWeather);
        
        dBc.analyse(3);
    }
}
