/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kostenko.fadviser;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;

/**
 *
 * @author Pavel
 */
@Entity
public class Weather implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(javax.persistence.TemporalType.DATE)

    protected Calendar date;

    String provider;

    float maxTemp;

    int humidity;

    String type;

    public Weather() {

    }

    public Weather(String p, Calendar d, float t, int h, String tp) {
        setProvider(p);
        setDate(d);
        setMaxTemp(t);
        setHumidity(h);
        setType(tp);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Calendar getDate() {
        return date;
    }

    public String getProvider() {
        return provider;
    }

    public float getMaxTemp() {
        return maxTemp;
    }

    public int getHumidity() {
        return humidity;
    }

    public String getType() {
        return type;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setMaxTemp(float maxTemp) {
        this.maxTemp = maxTemp;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Weather)) {
            return false;
        }
        Weather other = (Weather) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        DateFormat dF = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
        String dateInString = dF.format(this.getDate().getTime());
        String result = String.format("%s (%s): ***** [TEMP=%.1f] [HUMIDITY=%d%%] [TYPE=%s]\n",
                getProvider(),
                dateInString,
                getMaxTemp(),
                getHumidity(),
                getType());
        return result;
    }
}
