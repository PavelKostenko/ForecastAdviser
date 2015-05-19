/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kostenko.fadviser;

/**
 *
 * @author Pavel
 */
public class ForecastAdviser {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        XMLParser parser = new XMLParser();
        DBConnector dBc = new DBConnector();
        Test test = new Test();
        
//        Get data about weather for tomorrow from all providers
        Weather weatherFromOpenweather = parser.parseDocumentOPENWEATHER();
        
        Weather weatherFromYandex = parser.parseDocumentYANDEX();
        
        Weather weatherFromWeathercoua = parser.parseDocumentWEATHERCOUA();
        parser.parseDocumentWEATHERCOUAtest();
        
        Weather weatherFromYahoo = parser.parseDocumentYAHOO();
        
//        Get actual data for today
        Weather actualWeather = parser.actualWeatherFromYAHOO();
      
//        Persist the data in DB:
//        dBc.writeToDB(weatherFromOpenweather);
//        dBc.writeToDB(weatherFromYandex);
//        dBc.writeToDB(weatherFromWeathercoua);
//        dBc.writeToDB(weatherFromYahoo);
//        dBc.writeToDB(actualWeather);
  
//        Printing all entries from DB
//        test.showAllWeather();
        
//        We want to analyze last 3 days:
//        dBc.analyse(3);
    }
}
