package ua.ies.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.ies.project.model.BodyTemperature;

@Repository
public interface BodyTemperatureRepository extends JpaRepository<BodyTemperature, Long> { 
    BodyTemperature findTopByOrderByIdDesc();

}
