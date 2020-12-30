package ua.ies.project.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ua.ies.project.model.Co2;
import ua.ies.project.repository.Co2Repository;

@RestController
public class Co2RestController {
    
    @Autowired
    Co2Repository co2rep;

    @GetMapping("/co2/{id}")
    public Optional<Co2> co2ById(@PathVariable Long id) {
        return co2rep.findById(id);
    }

    @GetMapping("/co2")
    public List<Co2> co2All() {
        return co2rep.findAll();
    }

    @GetMapping("/co2/sensorid/{sensorid}")
    public List<Co2> co2BySensorId(@PathVariable Long sensorid) {
        return co2rep.findBySensorId(sensorid);
    }

}
