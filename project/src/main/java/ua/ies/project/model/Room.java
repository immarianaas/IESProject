package ua.ies.project.model;

import java.util.*;
import javax.persistence.*;

@Entity
@Table(name = "room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }


    @Column(name = "room_number", nullable = false)
    private int room_number;
    public int getRoom_number() { return room_number; }
    public void setRoom_number(int room_number) { this.room_number = room_number; }


    @Column(name = "floorNumber", nullable = false)
    private int floorNumber;
    public int getFloorNumber() { return floorNumber; }
    public void setFloorNumber(int floorNumber) { this.floorNumber = floorNumber; }


    @Column(name = "maxOccupation", nullable = true)
    private int maxOccupation;
    public int getMaxOccupation() { return maxOccupation; }
    public void setMaxOccupation(int maxOccupation) { this.maxOccupation = maxOccupation; }

    @Column(name = "maxLevelCo2", nullable = true)
    private double maxLevelCo2;
    public double getMaxLevelCo2() { return maxLevelCo2; }
    public void setMaxLevelCo2(double maxLevelCo2) { this.maxLevelCo2 = maxLevelCo2; }

    @Column(name = "maxTemperature", nullable = true)
    private double maxTemperature;
    public double getMaxTemperature() { return maxTemperature; }
    public void setMaxTemperature(double maxTemperature) { this.maxTemperature = maxTemperature; }



    @ManyToOne
    //@JoinColumn(name(mappedBy ="building")
    //@JoinColumn(name = "building", nullable = false)
    private Building building;
    public Building getBuilding() { return building; }
    public void setBuilding(Building building) { this.building = building; }

    /*
    @OneToMany(mappedBy = "room")
    private Set<User> users;
    public Set<User> getUsers() { return users; }
    public void setUsers(Set<User> users) { this.users = users; }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Co2 address;
    */

    @OneToMany(mappedBy = "room")
    private Set<Sensor> sensors;
    public Set<Sensor> getSensors() { return sensors; }
    public void setSensors(Set<Sensor> sensors) { this.sensors = sensors; }
    public void addSensor(Sensor s) {
        if (sensors == null) sensors = new HashSet<Sensor>();
        sensors.add(s);
    }

    public Room() {
    }

    public Room(int room_number, int floorNumber, int maxOccupation, Building building) {
        this.room_number = room_number;
        this.floorNumber = floorNumber;
        this.maxOccupation = maxOccupation;
        this.building = building;
    }

    public Map<String, Object> convertToMap() {
        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm.put("id", id);
        hm.put("room_number", room_number);
        hm.put("floorNumber", floorNumber);
        hm.put("maxOccupation", maxOccupation);
        hm.put("maxLevelCo2", maxLevelCo2);
        hm.put("maxTemperature", maxTemperature);
        return hm;
    }   

}

    

