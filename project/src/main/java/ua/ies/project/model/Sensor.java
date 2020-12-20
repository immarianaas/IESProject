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
@Table(name = "sensor")
@Inheritance(
    strategy = InheritanceType.JOINED
)
public class Sensor {
    private long id;
    private Date timestamp;


    private long sensorId;


    public Sensor() { }

    public Sensor(String timestamp, long sensorId) {

        this.sensorId = sensorId;
        this.timestamp = this.parseDate(timestamp);
    }

    public Sensor(Date timestamp,  long sensorId) {
        this.timestamp = timestamp;
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

    @Column(name="sensorId")
    public long getSensorId() { return sensorId; }
    public void setSensorId(long id) { 
        sensorId = id; }

    @Override
    public String toString() {
        return "[ sensor entry id= " + id + ": timestamp= " + timestamp + "; sensorId= " + sensorId + "; ]" ;
    }
    
    /* https://www.javaguides.net/2018/09/spring-boot-2-jpa-mysql-crud-example.html */


}