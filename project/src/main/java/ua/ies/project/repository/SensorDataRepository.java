package ua.ies.project.repository;

import org.springframework.stereotype.Repository;

import ua.ies.project.model.SensorData;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long>{
    
}
