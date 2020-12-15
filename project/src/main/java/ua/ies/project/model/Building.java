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
    private int buildingName;

    @Column(name = "localization", nullable = false)
    private int localization;

    @ManyToMany(mappedBy = "buildings")
    private Set<User> users;

    public Building() {
    }

    public Building(long buildingID, String buildingName, String localization, Set<User> users) {
        this.buildingID = buildingID;
        this.buildingName = buildingName;
        this.localization = localization;
        this.users = users;
    }
    
}
