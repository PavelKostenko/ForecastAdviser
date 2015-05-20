/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kostenko.logic;

import com.kostenko.db.DBConnector;
import com.kostenko.db.Weather;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 *
 * @author Pavel
 */
public class Analyzer {

    DBConnector dBConnector = new DBConnector();

    public void analyse(int daysToAnalyze) {
        List<Weather> weatherList = dBConnector.readFromDB(daysToAnalyze);
        if (weatherList.isEmpty()) {
            System.out.println("No weather records for previous " + daysToAnalyze + " days");
        } else {
            int daysAnalyzed = 0;

            float tempDiffOpenweather = 0;
            float humidDiffOpenweather = 0;
            float tempDiffYandex = 0;
            float humidDiffYandex = 0;
            float tempDiffWeathercoua = 0;
            float humidDiffWeathercoua = 0;
            float tempDiffYahoo = 0;
            float humidDiffYahoo = 0;

            for (int i = 0; i < daysToAnalyze; i++) {
                Calendar date = Calendar.getInstance();
                date.add(Calendar.DAY_OF_YEAR, -i);
                DateFormat dF = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
                String todayInString = dF.format(date.getTime());

                float actualTemp;
                float actualHumidity;
                float futureTemp;
                float futureHumidity;
                
                List <Weather> actualWeatherList = weatherList
                        .stream()
                        .filter(weather -> dF.format(weather.getDate().getTime())
                                .equals(todayInString))
                        .filter(weather -> weather.getType().equals("actual"))
                        .collect(Collectors.toList());
                
                if (actualWeatherList.isEmpty()) {
                    System.out.println("No actual weather records for " + todayInString);
                } else {
                    Weather actualWeather = actualWeatherList.get(0);
                    actualTemp = actualWeather.getMaxTemp();
                    actualHumidity = actualWeather.getHumidity();
                    System.out.println("");
                    System.out.println(String.format("%s actual data for %s %.1fC %.1f%%",
                            actualWeather.getProvider(),
                            todayInString,
                            roundFloat(actualTemp, 1),
                            actualHumidity));
                    System.out.println("");
                    List<Weather> ForecastedWeatherlist = weatherList
                        .stream()
                        .filter(weather -> dF.format(weather.getDate().getTime())
                                .equals(todayInString))
                        .filter(weather -> weather.getType().equals("1dayforecast"))
                        .collect(Collectors.toList());
                    if (ForecastedWeatherlist.size() == 4) {
                        daysAnalyzed++;
                        for (int j = 0; j < ForecastedWeatherlist.size(); j++) {
                            Weather w = ForecastedWeatherlist.get(j);
                            String provider = w.getProvider();
                            futureTemp = w.getMaxTemp();
                            futureHumidity = w.getHumidity();

                            float diffTemp = Math.abs(actualTemp - futureTemp);
                            float diffHumid = Math.abs(actualHumidity - futureHumidity);
                            System.out.println(String.format("%s forecast for %s %.1fC %.1f%%. Difference is: %.1fC %.1f%%.",
                                    provider,
                                    todayInString,
                                    futureTemp,
                                    futureHumidity,
                                    roundFloat(diffTemp, 1),
                                    diffHumid));
                            switch (w.getProvider()) {
                                case "OPENWEATHER":
                                    tempDiffOpenweather = tempDiffOpenweather + diffTemp;
                                    humidDiffOpenweather = humidDiffOpenweather + diffHumid;
                                    break;
                                case "YANDEX":
                                    tempDiffYandex = tempDiffYandex + diffTemp;
                                    humidDiffYandex = humidDiffYandex + diffHumid;
                                    break;
                                case "WEATHERCOUA":
                                    tempDiffWeathercoua = tempDiffWeathercoua + diffTemp;
                                    humidDiffWeathercoua = humidDiffWeathercoua + diffHumid;
                                    break;
                                case "YAHOO":
                                    tempDiffYahoo = tempDiffYahoo + diffTemp;
                                    humidDiffYahoo = humidDiffYahoo + diffHumid;
                                    break;
                                default:
                                    System.out.println("Provider is unknown");
                                    break;
                            }
                        }
                    } else {
                        System.out.println("Not enough forecast data from all providers for " + todayInString);
                    }
                }
            }
            if (daysAnalyzed > 0) {
                System.out.println("");
                System.out.println("daysAnalyzed " + daysAnalyzed);
                System.out.println("");
                System.out.println("tempDiffOpenweather " + roundFloat(tempDiffOpenweather / daysAnalyzed, 1));
                System.out.println("humidDiffOpenweather " + roundFloat(humidDiffOpenweather / daysAnalyzed, 1));
                System.out.println("");
                System.out.println("tempDiffYandex " + roundFloat(tempDiffYandex / daysAnalyzed, 1));
                System.out.println("humidDiffYandex " + roundFloat(humidDiffYandex / daysAnalyzed, 1));
                System.out.println("");
                System.out.println("tempDiffWeathercoua " + roundFloat(tempDiffWeathercoua / daysAnalyzed, 1));
                System.out.println("humidDiffWeathercoua " + roundFloat(humidDiffWeathercoua / daysAnalyzed, 1));
                System.out.println("");
                System.out.println("tempDiffYahoo " + roundFloat(tempDiffYahoo / daysAnalyzed, 1));
                System.out.println("humidDiffYahoo " + roundFloat(humidDiffYahoo / daysAnalyzed, 1));
            }
        }
    }

    private float roundFloat(float f, int n) {
        float multi = (float) Math.pow(10, n);
        return Math.round(f * multi) / multi;
    }

    public void printWeather(List<Weather> list) {
        for (Weather w : list) {
            System.out.println(w);
        }
    }
}
