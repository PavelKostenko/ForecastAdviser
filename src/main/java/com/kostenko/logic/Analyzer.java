/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kostenko.logic;

import com.kostenko.db.DBConnector;
import com.kostenko.db.Weather;
import com.kostenko.gui.ChartPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Pavel
 */
public class Analyzer {

    DBConnector dBConnector = new DBConnector();

    int daysAnalyzed = 0;

    float tempDiffOpenweather = 0;
    float humidDiffOpenweather = 0;
    float tempDiffYandex = 0;
    float humidDiffYandex = 0;
    float tempDiffWeathercoua = 0;
    float humidDiffWeathercoua = 0;
    float tempDiffYahoo = 0;
    float humidDiffYahoo = 0;

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
        }
    }

    public void analyzeInTextMode(int daysToAnalyze) {
        getDifference(daysToAnalyze);
        printStatistics();
    }

    public void analyzeInGUIMode(int daysToAnalyze) {
        getDifference(daysToAnalyze);

        JFrame f = new JFrame();
        f.setSize(400, 300);
        
        double[] values = new double[4];
        String[] names = new String[4];
        
        values[0] = roundFloat(tempDiffOpenweather / daysAnalyzed, 1);
        names[0] = "www.openweather.com";

        values[1] = roundFloat(tempDiffYandex / daysAnalyzed, 1);
        names[1] = "www.yandex.ru";

        values[2] = roundFloat(tempDiffWeathercoua / daysAnalyzed, 1);
        names[2] = "www.weather.co.ua";
        
        values[3] = roundFloat(tempDiffYahoo / daysAnalyzed, 1);
        names[3] = "www.yahoo.com";
        
        f.getContentPane().add(new ChartPanel(values, names, "Temperature forecast fault:"));
        
        WindowListener wndCloser = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        };
        
        
        f.addWindowListener(wndCloser);
        f.setVisible(true);
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
