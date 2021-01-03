package ua.ies.project.repository;

import org.springframework.stereotype.Repository;

import ua.ies.project.model.SensorData;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long>{
    //tipos de sensores num determinado quarto



    
}
