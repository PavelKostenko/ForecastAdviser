/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kostenko.main;

import com.kostenko.db.DBConnector;
import com.kostenko.db.Weather;
import com.kostenko.gui.MainWindow;
import com.kostenko.logic.Analyzer;
import com.kostenko.parsers.WeathercouaParser;
import com.kostenko.parsers.WeatherParser;
import com.kostenko.parsers.YahooParser;
import com.kostenko.parsers.OpenweatherParser;
import com.kostenko.parsers.StubParser;
import com.kostenko.parsers.YandexParser;

/**
 *
 * @author Pavel
 */
public class ForecastAdviser {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        DBConnector dBConnector = new DBConnector();
        Analyzer analyzer = new Analyzer();
        
//        Get data about weather for tomorrow from all providers
        Weather openweatherForecast = new OpenweatherParser().getFutureWeather();
        Weather yandexForecast = new YandexParser().getFutureWeather();
        Weather weathercouaForecast = new WeathercouaParser().getFutureWeather();
        Weather yahooForecast = new YahooParser().getFutureWeather();
        
//        Get actual data for today
        Weather actualWeather = WeatherParser.getActualWeather();
      
//        Persist the data in DB:
        dBConnector.writeToDB(openweatherForecast);
        dBConnector.writeToDB(yandexForecast);
        dBConnector.writeToDB(weathercouaForecast);
        dBConnector.writeToDB(yahooForecast);
        dBConnector.writeToDB(actualWeather);
  
//      Filling empty DB with random weather data if needed:
        
//        StubParser stubParser = new StubParser();
//        for (Weather w: stubParser.getStubWeather(3)){
//            dBConnector.writeToDB(w);
//        }

        new MainWindow(3);
        
//        Test test = new Test();
//        test.yahooParsing();
    }
}
