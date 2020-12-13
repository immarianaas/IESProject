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
@Table(name = "peoplecounter")
public class PeopleCounter {
    private long id;
    private Date timestamp;
    private double value;
    private String local;
    private int sensorId;

    public PeopleCounter() {
    }

    public PeopleCounter(String timestamp, int value, String local, int sensorId) {
        //this.timestamp = timestamp;
        this.value = value;
        this.local = local;
        this.sensorId = sensorId;
        this.timestamp = this.parseDate(timestamp);
    }

    public PeopleCounter(Date timestamp, int value, String local, int sensorId) {
        this.timestamp = timestamp;
        this.value = value;
        this.local = local;
        this.sensorId = sensorId;
    }

    private Date parseDate(String date) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmmmmm");
        
        try {
            Date d = formatter.parse(date);
            //System.out.println(d);
            return d;
        } catch( Exception e) {
            e.printStackTrace();
        }
        return null;
      }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }


    @Column(name="timestamp")
    public Date getTimestamp() { return timestamp; }
    @JsonProperty
    public void setTimestamp(String ts) {  this.timestamp = this.parseDate(ts); }
    public void setTimestamp(Date ts) { this.timestamp = ts; }

    @Column(name="value")
    public Double getValue() { return value; }
    public void setValue(double val) {
         value = val; }

    @Column(name="local")
    public String getLocal() { return local; }
    public void setLocal(String local) { 
        this.local = local; }

    @Column(name="sensorId")
    public int getSensorId() { return sensorId; }
    public void setSensorId(int id) { 
        sensorId = id; }

    @Override
    public String toString() {
        return "[ peoplecounter entry id= " + id + ": timestamp= " + timestamp.toString() + "; local= " + local + "; sensorId= " + sensorId + "; VALUE= " + value + " ]" ;
    }
    
    /* https://www.javaguides.net/2018/09/spring-boot-2-jpa-mysql-crud-example.html */
    
}
