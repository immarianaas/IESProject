package ua.ies.project.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;
     
    /*
    @ManyToMany(cascade = { CascadeType.ALL})
    @JoinTable(
        name = "user_roles",
        joinColumns = { @JoinColumn(name = "role_id")},
        inverseJoinColumns = { @JoinColumn(name = "user_id")}
    )
    */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
