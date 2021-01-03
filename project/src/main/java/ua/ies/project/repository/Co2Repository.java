
package ua.ies.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ua.ies.project.model.Co2;

@Repository
public interface Co2Repository extends JpaRepository<Co2, Long> { 

    Co2 findTopByOrderByIdDesc();

    List<Co2> findBySensorId(Long id);


}