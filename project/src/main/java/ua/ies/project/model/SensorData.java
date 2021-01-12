package ua.ies.project.model;


import java.text.SimpleDateFormat;

import java.util.*;
import javax.persistence.*;


import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

@Entity
@Table(name = "sensorData")
@Inheritance(
    strategy = InheritanceType.JOINED
)
public class SensorData {
    

    // private long sensorId;

    public SensorData() { }


    public SensorData(String timestamp, Sensor sensor, boolean warn) {

        this.sensor = sensor;
        this.timestamp = parseDate(timestamp);
        this.warn = warn;
    }

    public SensorData(Date timestamp,  Sensor sensor, boolean warn) {
        this.sensor = sensor;
        this.timestamp = timestamp;
        this.warn = warn;
    }

    public static Date parseDate(String date) {

        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmmmmm");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); 
        
        try {
            // System.out.println("\n\n\nbefore: "+date);
            Date d = formatter.parse(date);
            // System.out.println("after: "+ d + "\n\n\n");

            System.out.println("\n\n\n\n" + d + "\n\n\n\n\n");
            return d;
        } catch( Exception e) {
            e.printStackTrace();
        }
        return null;
      }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }


    @Column(name="timestamp")
    private Date timestamp;
    public Date getTimestamp() { return timestamp; }
    @JsonProperty
    public void setTimestamp(String ts) { 
        this.timestamp = parseDate(ts); 
        //System.out.println("\n\n\n\nhere a ser guardado; ts: "+this.timestamp+"\n\n\n\n");

        //System.out.println("\n\n\n\nco2 ts: " +this.timestamp);
    }
    public void setTimestamp(Date ts) { this.timestamp = ts; 
        //System.out.println("\n\n\n\nhere; ts: "+ts+"\n\n\n\n");
     
    }

    /*
    @Column(name="sensorId")
    public long getSensorId() { return sensorId; }
    public void setSensorId(long id) { 
        sensorId = id; }
    */

    @JsonIgnore
    @Column(name="warn")
    private boolean warn;

    public boolean getWarn() { return warn; }
    public void setWarn(boolean warn) { this.warn = warn; }

    @ManyToOne()
    private Sensor sensor;
    public Sensor getSensor() { return sensor; }
    public void setSensor(Sensor s) { sensor = s; }

    

    @Override
    public String toString() {
        return "[ sensor entry id= " + id + ": timestamp= " + timestamp + "; sensor= " + sensor + "; ]" ;
    }

    /*
    @ManyToOne()
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    */
    
    /* https://www.javaguides.net/2018/09/spring-boot-2-jpa-mysql-crud-example.html */


    public Map<String, Object> convertToMap() {
        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm.put("id", id);
        hm.put("timestamp", timestamp);
        hm.put("warn", warn);
        return hm;
    }   


}