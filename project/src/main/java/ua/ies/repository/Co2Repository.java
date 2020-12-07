package ua.ies.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.ies.model.Co2;

@Repository
public interface Co2Repository extends JpaRepository<Co2, Long> { }
