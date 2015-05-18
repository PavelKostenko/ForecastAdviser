/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kostenko.fadviser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author Pavel
 */
public class DBConnector {
    EntityManagerFactory eMF = Persistence.createEntityManagerFactory("ForecastAdviserPU");
    EntityManager em = eMF.createEntityManager();
    
    public void writeToDB(Weather w){
        Query query = em.createQuery("SELECT e FROM Weather AS e WHERE e.date=:d and e.type=:t and e.provider=:p").setParameter("d",w.getDate()).setParameter("t",w.getType()).setParameter("p", w.getProvider());
        List <Weather> list = query.getResultList();
        try {
            em.getTransaction().begin();
            if (list.isEmpty()){
                em.persist(w);
            } else {
                Weather x = list.get(0);
                if (!x.getType().equals("actual")){
                    x.setMaxTemp(w.getMaxTemp());
                    x.setHumidity(w.getHumidity());
                } else {
                    if (x.getMaxTemp()<w.getMaxTemp()){
                        x.setMaxTemp(w.getMaxTemp());
                    }
                    if (x.getHumidity()<w.getHumidity()){
                        x.setHumidity(w.getHumidity());
                    }
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }
    
    public void analyse(int daysToAnalyze){
        int daysAnalyzed=0;
        
        float tempDiffOpenweather = 0;
        float humidDiffOpenweather = 0;
        float tempDiffYandex = 0;
        float humidDiffYandex = 0;
        float tempDiffWeathercoua = 0;
        float humidDiffWeathercoua = 0;
        float tempDiffYahoo = 0;
        float humidDiffYahoo = 0;
        
        for(int i=0;i<daysToAnalyze;i++){
            Calendar date = Calendar.getInstance();
            date.add(Calendar.DAY_OF_YEAR, -i);
            DateFormat dF = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
            String todayInString = dF.format(date.getTime());

            float actualTemp;
            float actualHumidity;
            float futureTemp;
            float futureHumidity;
            
            Query query = em.createQuery("SELECT e FROM Weather AS e WHERE e.date=:d and e.type=:t").setParameter("d",date).setParameter("t", "actual");
            List <Weather> list = query.getResultList();
            if (list.isEmpty()){
                System.out.println("No actual weather records for " + todayInString);
            } else {
                actualTemp = list.get(0).getMaxTemp();
                actualHumidity = list.get(0).getHumidity();
                System.out.println("");
                System.out.println(list.get(0).provider + " actual data for " + todayInString + " " + roundFloat(actualTemp,1) + "C " + actualHumidity + "%");
                System.out.println("");
                Query query2 = em.createQuery("SELECT e FROM Weather AS e WHERE e.date=:d and e.type=:t").setParameter("d",date).setParameter("t", "1dayforecast");
                List <Weather> list2 = query2.getResultList();
                if (list2.size()==4){
                    daysAnalyzed++;
                    for(int j = 0;j<list2.size();j++){
                        Weather w = list2.get(j);
                        String provider = w.getProvider(); 
                        futureTemp = w.getMaxTemp();
                        futureHumidity = w.getHumidity();
                        
                        float diffTemp = Math.abs(actualTemp-futureTemp);
                        float diffHumid = Math.abs(actualHumidity - futureHumidity);
                        System.out.println(provider + " forecast for " + todayInString + " " + futureTemp + "C " + futureHumidity + "%. Difference is: " + roundFloat(diffTemp,1) + "C " + diffHumid + "%.");
                        switch (w.provider){
                            case "OPENWEATHER":
                                tempDiffOpenweather = tempDiffOpenweather+diffTemp;
                                humidDiffOpenweather = humidDiffOpenweather+diffHumid;
                                break;
                            case "YANDEX":
                                tempDiffYandex = tempDiffYandex+diffTemp;
                                humidDiffYandex = humidDiffYandex+diffHumid;
                                break;
                            case "WEATHERCOUA":
                                tempDiffWeathercoua = tempDiffWeathercoua+diffTemp;
                                humidDiffWeathercoua = humidDiffWeathercoua+diffHumid;
                                break;
                            case "YAHOO":
                                tempDiffYahoo = tempDiffYahoo+diffTemp;
                                humidDiffYahoo = humidDiffYahoo+diffHumid;
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
        if (daysAnalyzed>0){
            System.out.println("");
            System.out.println("daysAnalyzed " + daysAnalyzed);
            System.out.println("");
            System.out.println("tempDiffOpenweather " + roundFloat(tempDiffOpenweather/daysAnalyzed,1));
            System.out.println("humidDiffOpenweather " + roundFloat(humidDiffOpenweather/daysAnalyzed,1));
            System.out.println("");
            System.out.println("tempDiffYandex " + roundFloat(tempDiffYandex/daysAnalyzed,1));
            System.out.println("humidDiffYandex " + roundFloat(humidDiffYandex/daysAnalyzed,1));
            System.out.println("");
            System.out.println("tempDiffWeathercoua " + roundFloat(tempDiffWeathercoua/daysAnalyzed,1));
            System.out.println("humidDiffWeathercoua " + roundFloat(humidDiffWeathercoua/daysAnalyzed,1));
            System.out.println("");
            System.out.println("tempDiffYahoo " + roundFloat(tempDiffYahoo/daysAnalyzed,1));
            System.out.println("humidDiffYahoo " + roundFloat(humidDiffYahoo/daysAnalyzed,1));
        }
    }
    
    private float roundFloat(float f, int n){
        float multi = (float)Math.pow(10, n);
        return Math.round(f*multi)/multi;
    }
    
    public void test(){
            
        Query query3 = em.createQuery("SELECT e FROM Weather as e");
        List <Weather> list3 = query3.getResultList();
        for(Weather l:list3){
            System.out.println(l);
        }
    }
}