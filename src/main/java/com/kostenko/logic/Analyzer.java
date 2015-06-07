/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kostenko.logic;

import com.kostenko.db.DBConnector;
import com.kostenko.db.Weather;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Pavel
 */
public class Analyzer {

    DBConnector dBConnector = new DBConnector();

    public int daysAnalyzed = 0;

    public float tempDiffOpenweather = 0;
    public float humidDiffOpenweather = 0;
    public float tempDiffYandex = 0;
    public float humidDiffYandex = 0;
    public float tempDiffWeathercoua = 0;
    public float humidDiffWeathercoua = 0;
    public float tempDiffYahoo = 0;
    public float humidDiffYahoo = 0;

    public final String [] NAMES = {"www.openweather.com","www.yandex.ru","www.weather.co.ua","www.yahoo.com"};
    public double [] humidities = new double [4];
    public double [] temperatures = new double [4];
    
    public void getDifference(int daysToAnalyze) {
        List<Weather> weatherList = dBConnector.readFromDB(daysToAnalyze);
        if (weatherList.isEmpty()) {
            System.out.println("No weather records for previous " + daysToAnalyze + " days");
        } else {

            for (int i = 0; i < daysToAnalyze; i++) {

                LocalDate date = LocalDate.now().plusDays(-i);

                float actualTemp;
                float actualHumidity;
                float futureTemp;
                float futureHumidity;

                List<Weather> actualWeatherList = weatherList
                        .stream()
                        .filter(weather -> weather.getWeatherDate().equals(date))
                        .filter(weather -> weather.getType().equals("actual"))
                        .collect(Collectors.toList());

                if (actualWeatherList.isEmpty()) {
                    System.out.println("No actual weather records for " + date.toString());
                } else {
                    Weather actualWeather = actualWeatherList.get(0);
                    actualTemp = actualWeather.getMaxTemp();
                    actualHumidity = actualWeather.getHumidity();
                    System.out.println(String.format("\n%s actual data for %s %.1fC %.1f%%\n",
                            actualWeather.getProvider(),
                            date.toString(),
                            roundFloat(actualTemp, 1),
                            actualHumidity));
                    List<Weather> ForecastedWeatherlist = weatherList
                            .stream()
                            .filter(weather -> weather.getWeatherDate().equals(date))
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
                                    date.toString(),
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
                        System.out.println("Not enough forecast data from all providers for " + date.toString());
                    }
                }
            }
            if (daysAnalyzed>0){
                temperatures[0] = roundFloat(tempDiffOpenweather / daysAnalyzed, 1);
                temperatures[1] = roundFloat(tempDiffYandex / daysAnalyzed, 1);
                temperatures[2] = roundFloat(tempDiffWeathercoua / daysAnalyzed, 1);
                temperatures[3] = roundFloat(tempDiffYahoo / daysAnalyzed, 1);
                
                humidities[0] = roundFloat (humidDiffOpenweather / daysAnalyzed, 1);
                humidities[1] = roundFloat (humidDiffYandex / daysAnalyzed, 1);
                humidities[2] = roundFloat (humidDiffWeathercoua / daysAnalyzed, 1);
                humidities[3] = roundFloat (humidDiffYahoo / daysAnalyzed, 1);
                
            }
        }
    }

    public void analyzeInTextMode(int daysToAnalyze) {
        getDifference(daysToAnalyze);
        printStatistics();
    }

    private void printStatistics() {
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
