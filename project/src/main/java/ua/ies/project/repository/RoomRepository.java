package ua.ies.project.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.ies.project.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> { 

}
