package ua.ies.project.model;

import javax.persistence.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Transient
    private String passwordConfirm;

    @ManyToMany()
    private Set<Role> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role r) {
        if (roles==null) roles = new HashSet<Role>();
        roles.add(r);
    }

    /*
    @ManyToOne
    @JoinColumn(name="building_id")
    private Building building;
    */

    @ManyToMany
    private Set<Building> buildings;
    //@JoinColumn(name="building_id")

    public Set<Building> getBuildings() { return buildings; }
    public void setBuildings(Set<Building> b) { buildings = b; }
    public void addBuilding(Building b) { 
        if (buildings == null)
            buildings = new HashSet<Building>();
        buildings.add(b);
    }


    public Map<String, Object> convertToMap() {
        HashMap<String, Object> hm = new HashMap<String, Object>();
        hm.put("id", id);
        hm.put("username", username);
        //hm.put("password", password);
        //hm.put("passwordConfirm", passwordConfirm);
        return hm;
    }

	public User orElseThrow(Object object) {
		return null;
	}
}


