package ua.ies.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.ies.project.model.Building;
import ua.ies.project.model.Room;
import java.util.List;


@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
}




