package ua.ies.project.model;

import javax.persistence.*;

@Entity
@Table(name = "bodyTemperature")
public class BodyTemperature extends Sensor{

    private double value;


    public BodyTemperature() { 
        super();
    }

    public BodyTemperature( double value, String timestamp, long sensorId) {
        super( timestamp,  sensorId);
        this.value = value;
 
    }


    @Column(name="value")
    public double getValue() { return value; }
    public void setValue(double val) {
         value = val; }

    

    @Override
    public String toString() {
        return "[ co2 entry id= " + super.getId() + ": timestamp= " + super.getTimestamp() +  "; sensorId= " + super.getSensorId() + "; VALUE= " + value + " ]" ;
    }
    
    /* https://www.javaguides.net/2018/09/spring-boot-2-jpa-mysql-crud-example.html */
    
}
