package ua.ies.project.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import ua.ies.project.model.Sensor;
import ua.ies.project.model.SensorData;
import ua.ies.project.repository.SensorRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;


@RestController
public class SensorRestController {
    @Autowired
    private SensorRepository sensrep;


    public static EntityModel<Map<String, Object>> getSensorEntityModel(Sensor s) {
        Map<String, Object> s_map = s.convertToMap();
        Long id = s.getId();
        return EntityModel.of(s_map,
            linkTo(methodOn(RoomRestController.class).roomById(s.getRoom().getId())).withRel("room"),
            linkTo(methodOn(SensorRestController.class).sensordataByIdOfSensor(id)).withRel("sensordata"),
            linkTo(methodOn(SensorRestController.class).sensorById(id)).withSelfRel() 
            );
        }

    
    @GetMapping("/api/sensors")
    public List<EntityModel<Map<String, Object>>> allSensors() {
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (Sensor s : sensrep.findAll()) {
            l.add(getSensorEntityModel(s));
        }
        return l;
    }

    @GetMapping("/api/sensors/sensorid/{id}") // TODO se for admin, permitir todos, se nao, apenas os correspondentes
    public EntityModel<Map<String, Object>> sensorBySensorId(@PathVariable Long id) {
        Sensor s = sensrep.findOneBySensorId(id);
        return getSensorEntityModel(s);
    }
    

    @GetMapping("/api/sensors/{id}") // TODO se for admin, permitir todos, se nao, apenas os correspondentes
    public EntityModel<Map<String, Object>> sensorById(@PathVariable Long id) {
        Sensor s = sensrep.findById(id).orElseThrow();
        return getSensorEntityModel(s);
    }

    @GetMapping("/api/sensors/{id}/sensordata")
    public List<EntityModel<Map<String, Object>>> sensordataByIdOfSensor(@PathVariable Long id) {
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        Sensor s = sensrep.findById(id).orElseThrow();
        for (SensorData sd : s.getSensorsData()) {
            l.add(SensorDataRestController.getSensorDataEntityModel(sd));
        }
        return l;
    }

    @GetMapping("/api/sensors/sensorid/{id}/sensordata")
    public List<EntityModel<Map<String, Object>>> sensordataBySensorId(@PathVariable Long id) {
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        Sensor s = sensrep.findOneBySensorId(id);
        for (SensorData sd : s.getSensorsData()) {
            l.add(SensorDataRestController.getSensorDataEntityModel(sd));
        }
        return l;
    }
        
}
