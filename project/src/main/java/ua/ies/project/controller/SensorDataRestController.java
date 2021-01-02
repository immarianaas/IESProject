package ua.ies.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import ua.ies.project.model.SensorData;
import ua.ies.project.repository.SensorDataRepository;
import ua.ies.project.repository.SensorRepository;

import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class SensorDataRestController {

    @Autowired
    SensorDataRepository sensdatarep;

    public static EntityModel<Map<String, Object>> getSensorDataEntityModel(SensorData sd) {
        Map<String, Object> sd_map = sd.convertToMap();
        Long id = sd.getId();
        return EntityModel.of(sd_map,
            linkTo(methodOn(SensorRestController.class).sensorById(sd.getSensor().getId())).withRel("sensor"),
            linkTo(methodOn(SensorDataRestController.class).sensordataById(id)).withSelfRel() // <- change
            );
    }

    @GetMapping("/api/sensordata/{id}")
    public EntityModel<Map<String, Object>> sensordataById(@PathVariable Long id) {
        SensorData sd = sensdatarep.getOne(id);
        return getSensorDataEntityModel(sd);
    }

    @GetMapping("/api/sensordata/all")
    public List<EntityModel<Map<String, Object>>> allSensordata() {
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();

        for (SensorData sd : sensdatarep.findAll()) {
            l.add(getSensorDataEntityModel(sd));
        }
        return l;
    }

}

