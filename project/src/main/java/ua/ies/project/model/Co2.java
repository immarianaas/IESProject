package ua.ies.project.model;


import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import javax.persistence.*;
import javax.swing.text.DateFormatter;

import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "co2")
public class Co2 extends SensorData {

    public Co2() { 
        super();
    }

    public Co2( double value, String timestamp, Sensor sensor, boolean warn) {
        super(timestamp,  sensor, warn);
        this.value = value;
 
    }

    public Co2( double value, Date timestamp, Sensor sensor, boolean warn) {
        super(timestamp,  sensor, warn);
        this.value = value;
 
    }


    @Column(name="value")
    private double value;
    public double getValue() { return value; }
    public void setValue(double val) {
         value = val; }

    

    @Override
    public String toString() {
        return "[ co2 entry id= " + super.getId() + ": timestamp= " + super.getTimestamp() +  "; sensor= " + super.getSensor() + "; VALUE= " + value + " ]" ;
    }
    
    /* https://www.javaguides.net/2018/09/spring-boot-2-jpa-mysql-crud-example.html */
    
}
