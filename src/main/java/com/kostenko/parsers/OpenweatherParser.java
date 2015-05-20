/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kostenko.parsers;

import com.kostenko.db.Weather;
import java.util.Calendar;
import org.w3c.dom.Node;

/**
 *
 * @author Pavel
 */
public class OpenweatherParser extends WeatherParser {
    public Weather getFutureWeather() {

        Node n = getDocumentFromXML(OPENWEATHER_XML);

        float maxTempValue = MAX_INIT_TEMP;
        int humid = MAX_INIT_TEMP;

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
}
