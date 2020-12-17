package ua.ies.project.model;

import javax.persistence.*;
import java.util.Set;


@Entity
@Table(name = "building")

public class Building {
    @Id
    @Column(name = "buildingID", nullable = false)
    private long buildingID;

    @Column(name = "buildingName", nullable = false)
    private String buildingName;

    @Column(name = "localization", nullable = false)
    private String localization;

    @OneToMany(mappedBy = "buildings")
    private Set<User> users;

    @OneToMany(mappedBy = "buildings")
    private Set<Room> rooms;


    public Building() {
    }

    public Building(long buildingID, String buildingName) {
        this.buildingID = buildingID;
        this.buildingName = buildingName;
      
    }
    
}
