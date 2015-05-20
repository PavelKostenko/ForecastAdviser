/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//testing changes 14:14
package com.kostenko.test;

import com.kostenko.db.Weather;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author Pavel
 */
public class Test {

    EntityManagerFactory eMF = Persistence.createEntityManagerFactory("ForecastAdviserPU");
    EntityManager em = eMF.createEntityManager();

    public void showAllWeather() {
        Query query3 = em.createQuery("SELECT e FROM Weather as e");
        List<Weather> list3 = query3.getResultList();
        for (Weather l : list3) {
            System.out.println(l);
        }
    }
}
