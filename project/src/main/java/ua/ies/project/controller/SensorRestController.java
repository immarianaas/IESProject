package ua.ies.project.controller;

import java.util.*;

import javax.management.BadAttributeValueExpException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ua.ies.project.model.Role;
import ua.ies.project.model.Sensor;
import ua.ies.project.model.SensorData;
import ua.ies.project.model.User;
import ua.ies.project.repository.SensorDataRepository;
import ua.ies.project.repository.SensorRepository;
import ua.ies.project.repository.UserRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;


@RestController
public class SensorRestController {
    @Autowired
    private SensorRepository sensrep;


    @Autowired
    private UserRepository userrep;

    public boolean checkIfAdmin(String uname) {
        User u = userrep.findByUsername(uname);
        if (u.getRoles() != null)
            for (Role r : u.getRoles()) 
                if (r.getName().equals("admin")) return true;
        return false;
    }


    public boolean checkIfMine(String uname, long sens_id) {
        //User u = userrep.findByUsername(uname);
        Sensor s = sensrep.findById(sens_id).get();
        for (User u : s.getRoom().getBuilding().getUsers()) {
            if (u.getUsername().equals(uname)) return true;
        }
        return false;
    }
    

    public static EntityModel<Map<String, Object>> getSensorEntityModel(String username, Sensor s) {
        Map<String, Object> s_map = s.convertToMap();
        Long id = s.getId();
        return EntityModel.of(s_map,
            linkTo(methodOn(RoomRestController.class).roomById(username, s.getRoom().getId())).withRel("room"),
            linkTo(methodOn(SensorRestController.class).sensordataByIdOfSensor(username, id, null, null)).withRel("sensordata"),
            linkTo(methodOn(SensorRestController.class).sensorById(username, id)).withSelfRel() 
            );
        }

    
    @GetMapping("/api/sensors/all") // -> admin
    public List<EntityModel<Map<String, Object>>> allSensors(@CurrentSecurityContext(expression="authentication.name") String username) {
        if (!(checkIfAdmin(username))) throw new AccessDeniedException("403 returned");

        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (Sensor s : sensrep.findAll()) {
            l.add(getSensorEntityModel(username, s));
        }
        return l;
    }

    /*
    @GetMapping("/api/sensors") // -> todos (mostra os meus) -> n feito, n sei se é util.
    public List<EntityModel<Map<String, Object>>> allMySensors(@CurrentSecurityContext(expression="authentication.name") String username) { // TODO a parte do 'my'
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (Sensor s : sensrep.findAll()) {
            l.add(getSensorEntityModel(username, s));
        }
        return l;
    }
    */

    @GetMapping("/api/sensors/sensorid/{id}") // -> admin ou meu
    public EntityModel<Map<String, Object>> sensorBySensorId(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable Long id) {
        if (!(checkIfAdmin(username)) && !(checkIfMine(username, id))) throw new AccessDeniedException("403 returned");

        Sensor s = sensrep.findOneBySensorId(id);
        if (s == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return getSensorEntityModel(username, s);
    }
    

    @GetMapping("/api/sensors/{id}") // -> admin ou meu
    public EntityModel<Map<String, Object>> sensorById(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable Long id) {
        if (!(checkIfAdmin(username)) && !(checkIfMine(username, id))) throw new AccessDeniedException("403 returned");

        Sensor s;
        try {
        s = sensrep.findById(id).orElseThrow();
        } catch (Exception e) {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND);    
        }
        return getSensorEntityModel(username, s);
    }

    @GetMapping("/api/sensors/{id}/sensordata") // -> admin ou meu
    public List<EntityModel<Map<String, Object>>> sensordataByIdOfSensor(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable Long id,
    @RequestParam(required = false) Integer pageNo,
    @RequestParam(required = false) Integer pageSize) {
        if (!(checkIfAdmin(username)) && !(checkIfMine(username, id))) throw new AccessDeniedException("403 returned");

        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        Sensor s;
        try {
        s = sensrep.findById(id).orElseThrow();
        } catch (Exception e) {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND);    
        }
        if (pageNo == null) pageNo = 0;
        if (pageSize == null) pageSize = 50;

        Page<SensorData> p = sensdatarep.findAll(
            PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "timestamp"))
        );
        for (SensorData sd : p) {
            l.add(SensorDataRestController.getSensorDataEntityModel(username, sd));
        }
        return l;
    }

    @GetMapping("/api/sensors/sensorid/{id}/sensordata") // -> admin ou meu
    public List<EntityModel<Map<String, Object>>> sensordataBySensorId(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable Long id,
    @RequestParam(required = false) Integer pageNo,
    @RequestParam(required = false) Integer pageSize) {
        if (!(checkIfAdmin(username)) && !(checkIfMine(username, id))) throw new AccessDeniedException("403 returned");

        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        Sensor s = sensrep.findOneBySensorId(id);
        if (s==null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);    

        if (pageNo == null) pageNo = 0;
        if (pageSize == null) pageSize = 50;

        Page<SensorData> p = sensdatarep.findAll(
            PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "timestamp"))
        );

        for (SensorData sd : p) {
            l.add(SensorDataRestController.getSensorDataEntityModel(username, sd));
        }
        return l;
    }

    /*
    // ------- UPDATE ------- (falta permissoes)

    @PutMapping("/api/sensors/{id}")
    public EntityModel<Map<String, Object>> updateSensorById(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable long id, @RequestBody Sensor newsensor)
            throws BadAttributeValueExpException {
                if (!(checkIfAdmin(username)) && !(checkIfMine(username, id))) throw new AccessDeniedException("403 returned");
        
                Sensor s;
                try {
                s = sensrep.findById(id).orElseThrow();
                } catch (Exception e) {
                   throw new ResponseStatusException(HttpStatus.NOT_FOUND);    
                }
            if (!(newsensor.getType().equals("CO2") || newsensor.getType().equals("PEOPLE_COUNTER") || newsensor.getType().equals("BODY_TEMPERATURE")))
        throw new BadAttributeValueExpException("400 invalid parameter value in 'type'");


        if (newsensor.getType() != null) s.setType(newsensor.getType());
        // n atualiza o sensorId.

        return getSensorEntityModel(username, sensrep.save(s));
    }
    */


    // ------- DELETE ------- (falta permissoes)
    @DeleteMapping("/api/sensors/{id}")
    public void deleteSensorById(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable long id) {
        if (!(checkIfAdmin(username)) && !(checkIfMine(username, id))) throw new AccessDeniedException("403 returned");
        
        Sensor s;
        try {
        s = sensrep.findById(id).orElseThrow();
        } catch (Exception e) {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND);    
        }
        sensdatarep.deleteAll(s.getSensorsData());
        s.getRoom().getSensors().remove(s);
        sensrep.delete(s);
        
    }


    @Autowired
    private SensorDataRepository sensdatarep;




        
}
