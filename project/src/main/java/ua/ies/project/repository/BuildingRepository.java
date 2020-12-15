package ua.ies.project.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.ies.project.model.PeopleCounter;

@Repository
public interface BuildingRepository extends JpaRepository<PeopleCounter, Long> { 
    PeopleCounter find_Building();

}
