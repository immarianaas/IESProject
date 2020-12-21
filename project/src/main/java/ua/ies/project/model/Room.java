package ua.ies.project.model;

import java.util.Set;

import javax.persistence.*;


@Entity
@Table(name = "room")
public class Room {
    private long id;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @ManyToOne
    //@JoinColumn(name(mappedBy ="building")
    @JoinColumn(name = "building", nullable = false)
    private Building Building;
    public Building getBuilding() { return Building; }
    public void setBuilding(Building Building) { this.Building = Building; }


    @OneToMany(mappedBy = "users")
    private Set<User> users;
    public Set<User> getUsers() { return users; }
    public void setUsers(Set<User> users) { this.users = users; }
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Co2 address;


    public Room() {
    }

    public Room(int room_number, int floorNumber, int maxOccupation, Building Building) {
        this.room_number = room_number;
        this.floorNumber = floorNumber;
        this.maxOccupation = maxOccupation;
        this.Building = Building;
    }

    

}

    

