package ua.ies.project.model;
import java.util.*;
import javax.persistence.*;


@Entity
@Table(name = "sensor")
public class Sensor {
    
    public Sensor(long id, long sensorId, Room room, Set<SensorData> sd) {
        this.id = id;
        this.sensorId = sensorId;
        this.room = room;
        this.sensorsData = sd;
    }
    public Sensor() {}

    
    @Column(name="type")
    private String type;
    public String getType() { return type; }
    public void getType(String t) { type = t; }


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }


    @ManyToOne()
    private Room room;
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }


    @Column(name="sensorId")
    private long sensorId;
    public long getSensorId() { return sensorId; }
    public void setSensorId(long id) { 
        sensorId = id; }


    @OneToMany(mappedBy = "sensor")
    private Set<SensorData> sensorsData;

    public Set<SensorData> getSensorsData() { return sensorsData; }
    public void setSensorsData(Set<SensorData> ls) {sensorsData = ls; }

    @Override
    public String toString() {
        return "[ sensor (db)-id= " + id + ": sensor_id = " + sensorId + "room= " + room + "; type= " + type + "; ]" ;
    }
}
