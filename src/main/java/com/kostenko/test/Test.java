/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//testing changes 14:14
package com.kostenko.test;

import com.kostenko.db.Weather;
import static com.kostenko.parsers.WeatherParser.getDocumentFromXML;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.w3c.dom.Node;

/**
 *
 * @author Pavel
 */
public class Test {

//    EntityManagerFactory eMF = Persistence.createEntityManagerFactory("ForecastAdviserPU");
//    EntityManager em = eMF.createEntityManager();
//
//    public void showAllWeather() {
//        Query query3 = em.createQuery("SELECT e FROM Weather as e");
//        List<Weather> list3 = query3.getResultList();
//        for (Weather l : list3) {
//            System.out.println(l);
//        }
//    }
//    
//    public void testingJava8Date(){
//        LocalDate date = LocalDate.now();
//        System.out.println(LocalDate.now().plusDays(1).toString());
////        System.out.println("2015-05-21".equals(date.toString()));
//    }
//    
    public void yahooParsing(){
        String s = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("d MMM yyyy", new Locale("en")));
        System.out.println(s);
    }
//    
//    public Weather getFutureWeather() {
//
//        Node n = getDocumentFromXML("http://api.openweathermap.org/data/2.5/forecast/daily?q=Tbilisi&mode=xml&units=metric&cnt=7");
//
//        float maxTempValue = MAX_INIT_TEMP;
//        int humid = MAX_INIT_TEMP;
//
//        Node weatherdata = getSubnode(n, "weatherdata");
//        Node forecast = getSubnode(weatherdata, "forecast");
//        Node time = getSubnodes(forecast, "time")
//                .stream()
//                .filter(node -> getAttribute(node, "day")
//                        .equals(SIMPLE_DATE_FORMAT.format(getTomorrow())))
//                .findAny().get();
//
//        Node temperature = getSubnode(time, "temperature");
//        maxTempValue = Float.parseFloat(getAttribute(temperature, "max"));
//
//        Node humidity = getSubnode(time, "humidity");
//        humid = Integer.parseInt(getAttribute(humidity, "value"));
//        
//        
//        
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DAY_OF_YEAR, 1);
//        Weather today = new Weather("OPENWEATHER", cal, maxTempValue, humid, "1dayforecast");
//        return today;
//        
//    }
}
