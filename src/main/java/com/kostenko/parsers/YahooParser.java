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
public class YahooParser extends WeatherParser {
    public Weather getFutureWeather() {

        Node n = getDocumentFromXML(YAHOO_XML);

        float maxTempValue = MAX_INIT_TEMP;
        int humid = MAX_INIT_HUMID;

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
}
