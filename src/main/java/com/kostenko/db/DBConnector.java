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
    
    public void writeToDB(Weather newWeatherBean){
        Query queryForExistingEntity = em.createQuery("SELECT e FROM Weather AS e WHERE e.weatherDate=:d and e.type=:t and e.provider=:p")
                .setParameter("d",newWeatherBean.getWeatherDate())
                .setParameter("t",newWeatherBean.getType())
                .setParameter("p", newWeatherBean.getProvider());
        List <Weather> listExistingEntities = queryForExistingEntity.getResultList();
        try {
            em.getTransaction().begin();
            if (listExistingEntities.isEmpty()){
                em.persist(newWeatherBean);
            } else {
                Weather existingWeatherBean = listExistingEntities.get(0);
                if (!existingWeatherBean.getType().equals("actual")){
                    existingWeatherBean.setMaxTemp(newWeatherBean.getMaxTemp());
                    existingWeatherBean.setHumidity(newWeatherBean.getHumidity());
                } else {
                    if (existingWeatherBean.getMaxTemp()<newWeatherBean.getMaxTemp()){
                        existingWeatherBean.setMaxTemp(newWeatherBean.getMaxTemp());
                    }
                    if (existingWeatherBean.getHumidity()<newWeatherBean.getHumidity()){
                        existingWeatherBean.setHumidity(newWeatherBean.getHumidity());
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
        
        Query queryReadFromDB = em.createQuery("SELECT e FROM Weather AS e WHERE e.weatherDate>:s and e.weatherDate<=:t")
                    .setParameter("s",LocalDate.now().plusDays(-daysToAnalyze))
                    .setParameter("t",LocalDate.now());
        return queryReadFromDB.getResultList();
    }
}