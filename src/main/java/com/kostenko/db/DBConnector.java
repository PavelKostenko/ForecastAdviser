/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kostenko.db;

import java.time.LocalDate;
import java.util.List;
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
        Query query = em.createQuery("SELECT e FROM Weather AS e WHERE e.weatherDate=:d and e.type=:t and e.provider=:p")
                .setParameter("d",w.getWeatherDate())
                .setParameter("t",w.getType())
                .setParameter("p", w.getProvider());
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
    
    public List <Weather> readFromDB(int daysToAnalyze){
        
        Query query = em.createQuery("SELECT e FROM Weather AS e WHERE e.weatherDate>:s and e.weatherDate<=:t")
                    .setParameter("s",LocalDate.now().plusDays(-daysToAnalyze))
                    .setParameter("t",LocalDate.now());
        return query.getResultList();
    }
}