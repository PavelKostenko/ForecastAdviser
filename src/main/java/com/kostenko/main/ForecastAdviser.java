/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kostenko.main;

import com.kostenko.db.DBConnector;
import com.kostenko.test.Test;
import com.kostenko.db.Weather;
import com.kostenko.parsers.WeathercouaParser;
import com.kostenko.parsers.WeatherParser;
import com.kostenko.parsers.YahooParser;
import com.kostenko.parsers.OpenweatherParser;
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
//        Test test = new Test();
        
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
  
//        Printing all entries from DB
//        test.showAllWeather();
        
//        We want to analyze last 3 days:
        dBConnector.analyse(3);
    }
}