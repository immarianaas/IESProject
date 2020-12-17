package ua.ies.project.model;

import javax.persistence.*;

@Entity
@Table(name = "room")
public class Room {
    @Id
    @Column(name = "roomID", nullable = false)
    private long roomID;

    @Column(name = "floorNumber", nullable = false)
    private int floorNumber;

    @Column(name = "maxOccupation", nullable = false)
    private int maxOccupation;

    @Column(name = "building", nullable = false)
    private Building Building;

    public Room() {
    }

    public Room(long roomID, int floorNumber, int maxOccupation, Building Building) {
        this.roomID = roomID;
        this.floorNumber = floorNumber;
        this.maxOccupation = maxOccupation;
        this.Building = Building;
    }

    

}

    

