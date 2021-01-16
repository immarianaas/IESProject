package ua.ies.project.repository;

import org.springframework.stereotype.Repository;

import ua.ies.project.model.SensorData;

import java.util.List;

// import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long>{
    //tipos de sensores num determinado quarto
    List<SensorData> findBySensorIdOrderByTimestampDesc(Long sensorid);
    List<SensorData> findBySensorIdOrderByTimestampAsc(Long sensorid);
	// Page<SensorData> findBySensorId(long id);
	// Page<SensorData> findBySensorId(long sensorId, PageRequest of);
	Page<SensorData> findBySensorId(long sensorId, Pageable pageable);
    


}
