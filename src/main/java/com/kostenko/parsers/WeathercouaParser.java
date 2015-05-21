/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kostenko.parsers;

import com.kostenko.db.Weather;
import static com.kostenko.parsers.WeatherParser.MAX_INIT_TEMP;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.w3c.dom.Node;

/**
 *
 * @author Pavel
 */
public class WeathercouaParser extends WeatherParser {
    public Weather getFutureWeather() {

        Node n = getDocumentFromXML(WEATHERCOUA_XML);

        float maxTempValue = MAX_INIT_TEMP;
        int minHumid = MIN_INIT_HUMID;
        int maxHumid = MAX_INIT_HUMID;

        Node forecastRoot = getSubnode(n, "forecast");

        Node forecastSub = getSubnode(forecastRoot, "forecast");

        List<Node> days = getSubnodes(forecastSub, "day")
                .stream()
                .filter(node -> getAttribute(node, "date")
                        .equals(LocalDate.now().plusDays(1).toString()))
                .collect(Collectors.toList());

        for (Node day : days) {
            Node t = getSubnode(day, "t");

            Node max = getSubnode(t, "max");
            float localMax = Float.parseFloat(max.getFirstChild().getNodeValue());
            maxTempValue = (maxTempValue < localMax) ? localMax : maxTempValue;

            Node hmid = getSubnode(day, "hmid");

            Node minH = getSubnode(hmid, "min");
            int localMinHumid = Integer.parseInt(minH.getFirstChild().getNodeValue());
            minHumid = (minHumid > localMinHumid) ? localMinHumid : minHumid;

            Node maxH = getSubnode(hmid, "max");
            int localMaxHumid = Integer.parseInt(maxH.getFirstChild().getNodeValue());
            maxHumid = (maxHumid < localMaxHumid) ? localMaxHumid : maxHumid;
        }
        int humid = (minHumid + maxHumid) / 2;
        Weather today = new Weather("WEATHERCOUA", LocalDate.now().plusDays(1), maxTempValue, humid, "1dayforecast");
        return today;
    }
}
