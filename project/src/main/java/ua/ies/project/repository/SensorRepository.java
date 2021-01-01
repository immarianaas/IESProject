
package ua.ies.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.ies.project.model.Room;
import ua.ies.project.model.Sensor;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> { 

    Sensor findOneBySensorId(long sensorId);

}
