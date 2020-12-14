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
public class Co2 {
    private long id;
    private Date timestamp;
    private double value;
    private String local;
    private int sensorId;

    public Co2() {
    }

    public Co2(String timestamp, int value, String local, int sensorId) {
        //this.timestamp = timestamp;
        this.value = value;
        this.local = local;
        this.sensorId = sensorId;
        this.timestamp = this.parseDate(timestamp);
    }

    public Co2(Date timestamp, int value, String local, int sensorId) {
        this.timestamp = timestamp;
        this.value = value;
        this.local = local;
        this.sensorId = sensorId;
    }

    private Date parseDate(String date) {

        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmmmmm");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); 
        
        try {
            System.out.println("\n\n\nbefore: "+date);
            Date d = formatter.parse(date);
            System.out.println("after: "+ d + "\n\n\n");
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
    public void setTimestamp(String ts) { 
        this.timestamp = this.parseDate(ts); 
        System.out.println("\n\n\n\nhere a ser guardado; ts: "+this.timestamp+"\n\n\n\n");

        //System.out.println("\n\n\n\nco2 ts: " +this.timestamp);
    }
    public void setTimestamp(Date ts) { this.timestamp = ts; 
        System.out.println("\n\n\n\nhere; ts: "+ts+"\n\n\n\n");
     
    }

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
        return "[ co2 entry id= " + id + ": timestamp= " + timestamp + "; local= " + local + "; sensorId= " + sensorId + "; VALUE= " + value + " ]" ;
    }
    
    /* https://www.javaguides.net/2018/09/spring-boot-2-jpa-mysql-crud-example.html */
    
}
