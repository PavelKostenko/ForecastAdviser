/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kostenko.parsers;

import com.kostenko.db.Weather;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author pavel
 */
public class StubParser {
    final String [] PROVIDERS = {"OPENWEATHER","YANDEX","WEATHERCOUA","YAHOO"};
    Random rand = new Random();
    public List <Weather> getStubWeather(int daysToFill){
        
        List <Weather> weathers = new ArrayList();
        for (int i=0;i<daysToFill;i++){
            float actualTemp = 25 + (rand.nextInt(11)-5);
            int actualHumid = 50 + (rand.nextInt(61)-30);
            
            for (String s:PROVIDERS){
                float temp = actualTemp +(rand.nextInt(11)-5);
                int humid = actualHumid+(rand.nextInt(41)-20);
                weathers.add(new Weather(s, LocalDate.now().minusDays(i), temp, humid, "1dayforecast"));
            }
            weathers.add(new Weather("YAHOO",LocalDate.now().minusDays(i),actualTemp,actualHumid,"actual"));
        }
        
        return weathers;
    }
}
