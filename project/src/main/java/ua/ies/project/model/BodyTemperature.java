package ua.ies.project.model;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "bodyTemperature")
public class BodyTemperature extends SensorData{

    private double value;


    public BodyTemperature() { 
        super();
    }

    public BodyTemperature( double value, String timestamp, Sensor sensor, boolean warn) {
        super( timestamp,  sensor, warn);
        this.value = value;
 
    }

    public BodyTemperature( double value, Date timestamp, Sensor sensor, boolean warn) {
        super( timestamp,  sensor, warn);
        this.value = value;
 
    }


    @Column(name="value")
    public double getValue() { return value; }
    public void setValue(double val) {
         value = val; }

    

    @Override
    public String toString() {
        return "[ co2 entry id= " + super.getId() + ": timestamp= " + super.getTimestamp() +  "; sensor= " + super.getSensor() + "; VALUE= " + value + " ]" ;
    }
    
    /* https://www.javaguides.net/2018/09/spring-boot-2-jpa-mysql-crud-example.html */
    
}
