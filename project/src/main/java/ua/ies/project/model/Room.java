package ua.ies.project.model;

import javax.persistence.*;

import org.hibernate.annotations.ForeignKey;

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


    @ForeignKey(name = "building")
    @Column(name = "building", nullable = false)
    private Building Building;
    public Building getBuilding() { return Building; }
    public void setBuilding(Building Building) { this.Building = Building; }


    public Room() {
    }

    public Room(long roomID, int floorNumber, int maxOccupation, Building Building) {
        this.roomID = roomID;
        this.floorNumber = floorNumber;
        this.maxOccupation = maxOccupation;
        this.Building = Building;
    }

    

}

    

