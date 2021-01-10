package ua.ies.project.model;

import javax.persistence.*;
import java.util.*;


@Entity
@Table(name = "building")
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }


    @Column(name = "buildingName", nullable = false)
    private String buildingName;
    
    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }

    @Column(name = "country", nullable = false)
    private String country;
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }


    
    @Column(name = "city", nullable = false)
    private String city;
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }


    @Column(name = "street", nullable = false)
    private String street;
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }


    @Column(name = "door_number", nullable = false)
    private int door_number;
    public int getDoor_number() { return door_number; }
    public void setDoor_number(int door_number) { this.door_number = door_number; }

    

    //@OneToMany(mappedBy = "building")
    @ManyToMany(mappedBy = "buildings")
    private Set<User> users;
    public Set<User> getUsers() { return users; }
    public void setUsers(Set<User> users) { this.users = users; }
    public void addUser(User user) { 
        if (users == null)
            users = new HashSet<User>();
        this.users.add(user); 
    }

    /*
    @OneToMany(mappedBy = "rooms")
    private Set<Room> rooms;
    public Set<Room> getRooms() { return rooms; }
    public void setRoom(Set<Room> rooms) { this.rooms = rooms; }
    */
    @OneToMany(mappedBy = "building")
    private Set<Room> rooms;
    
    public Set<Room> getRooms() { return rooms; }
    public void setRooms(Set<Room> rooms) { this.rooms = rooms; }
    public void addRoom(Room r) {
        if (rooms == null) rooms = new HashSet<Room>();
        rooms.add(r);
    }


    public Building() {
    }

    public Building(long id, String buildingName) {
        this.id = id;
        this.buildingName = buildingName;
      
    }

    
    public Map<String, Object> convertToMap() {
        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm.put("id", id);
        hm.put("buildingName", buildingName);
        hm.put("country", country);
        hm.put("city", city);
        hm.put("street", street);
        hm.put("door_number", door_number);
        return hm;
    }
    
}
