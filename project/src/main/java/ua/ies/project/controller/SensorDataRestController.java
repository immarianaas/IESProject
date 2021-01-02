package ua.ies.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import ua.ies.project.model.Role;
import ua.ies.project.model.SensorData;
import ua.ies.project.model.User;
import ua.ies.project.repository.SensorDataRepository;
import ua.ies.project.repository.SensorRepository;
import ua.ies.project.repository.UserRepository;

import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class SensorDataRestController {

    @Autowired
    SensorDataRepository sensdatarep;

    @Autowired
    UserRepository userrep;

    public boolean checkIfAdmin(String uname) {
        User u = userrep.findByUsername(uname);
        if (u.getRoles() != null)
            for (Role r : u.getRoles()) 
                if (r.getName().equals("admin")) return true;
        return false;
    }



    public boolean checkIfMine(String uname, long data_id) {
        //User u = userrep.findByUsername(uname);
        SensorData sd = sensdatarep.findById(data_id).get();
        for (User u : sd.getSensor().getRoom().getBuilding().getUsers()) {
            if (u.getUsername().equals(uname)) return true;
        }
        return false;
    }
    

    public static EntityModel<Map<String, Object>> getSensorDataEntityModel(String username, SensorData sd) {
        Map<String, Object> sd_map = sd.convertToMap();
        Long id = sd.getId();
        return EntityModel.of(sd_map,
            linkTo(methodOn(SensorRestController.class).sensorById(username, sd.getSensor().getId())).withRel("sensor"),
            linkTo(methodOn(SensorDataRestController.class).sensordataById(username, id)).withSelfRel() // <- change
            );
    }

    @GetMapping("/api/sensordata/{id}") // -> admin ou meu
    public EntityModel<Map<String, Object>> sensordataById(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable Long id) {
        if (!(checkIfAdmin(username)) && !(checkIfMine(username, id))) throw new AccessDeniedException("403 returned");

        SensorData sd = sensdatarep.getOne(id);
        return getSensorDataEntityModel(username, sd);
    }

    @GetMapping("/api/sensordata/all") // -> admin
    public List<EntityModel<Map<String, Object>>> allSensordata(@CurrentSecurityContext(expression="authentication.name") String username) {
        if (!checkIfAdmin(username)) throw new AccessDeniedException("403 returned");
        
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();

        for (SensorData sd : sensdatarep.findAll()) {
            l.add(getSensorDataEntityModel(username, sd));
        }
        return l;
    }

}
