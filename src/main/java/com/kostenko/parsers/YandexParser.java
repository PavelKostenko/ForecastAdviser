/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kostenko.parsers;

import com.kostenko.db.Weather;
import static com.kostenko.parsers.WeatherParser.MAX_INIT_TEMP;
import java.util.Calendar;
import org.w3c.dom.Node;

/**
 *
 * @author Pavel
 */
public class YandexParser extends WeatherParser {
    public Weather getFutureWeather() {

        Node n = getDocumentFromXML(YANDEX_XML);

        float maxTempValue = MAX_INIT_TEMP;
        float minTempValue = MIN_INIT_TEMP;
        int dayHumid = MAX_INIT_HUMID;
        int nightHumid = MIN_INIT_HUMID;

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
}
