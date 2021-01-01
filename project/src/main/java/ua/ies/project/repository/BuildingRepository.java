package ua.ies.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.ies.project.model.Building;



@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {

}




