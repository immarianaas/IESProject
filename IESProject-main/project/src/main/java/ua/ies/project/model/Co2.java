package ua.ies.project.model;

import java.sql.Time;

import javax.persistence.*;

@Entity
@Table(name="co2")
public class Co2 {
    private long id;
    private Time timestamp;
    private int value;

    public Co2() {}
    public Co2(Time ts, int value) {
        timestamp = ts;
        this.value = value;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    // TODO finish..
    // https://www.javaguides.net/2018/09/spring-boot-2-jpa-mysql-crud-example.html
    
}
