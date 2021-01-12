package ua.ies.project.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.management.BadAttributeValueExpException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.CurrentSecurityContext;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;


import ua.ies.project.model.Role;
import ua.ies.project.model.Room;
import ua.ies.project.model.Sensor;
import ua.ies.project.model.User;
import ua.ies.project.repository.BuildingRepository;
import ua.ies.project.repository.RoomRepository;
import ua.ies.project.repository.SensorDataRepository;
import ua.ies.project.repository.SensorRepository;
import ua.ies.project.repository.UserRepository;



@RestController
public class RoomRestController {
    @Autowired
    BuildingRepository buildrep;

    @Autowired
    RoomRepository roomrep;

    @Autowired
    SensorRepository sensrep;

    public boolean checkIfAdmin(String uname) {
        User u = userrep.findByUsername(uname);
        if (u.getRoles() != null)
            for (Role r : u.getRoles()) 
                if (r.getName().equals("admin")) return true;
        return false;
    }


    public boolean checkIfMine(String uname, long room_id) {
        //User u = userrep.findByUsername(uname);
        Room b = roomrep.findById(room_id).get();
        for (User u : b.getBuilding().getUsers()) {
            if (u.getUsername().equals(uname)) return true;
        }
        return false;
    }


    @GetMapping("/api/rooms/all") // -> admins
    public List<EntityModel<Map<String, Object>>> seeRooms(@CurrentSecurityContext(expression="authentication.name") String username) {
        if (!(checkIfAdmin(username))) throw new AccessDeniedException("403 returned");
        
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (Room r : roomrep.findAll()) {
            l.add(getRoomEntityModel(username, r));
        }

        return l;
    }

    @GetMapping("/api/rooms/{id}") // -> admin e meu
    public EntityModel<Map<String, Object>> roomById(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable Long id) {
        if (!(checkIfAdmin(username)) && !(checkIfMine(username, id))) throw new AccessDeniedException("403 returned");
        Room r = null;
        try {
        r = roomrep.findById(id).orElseThrow();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return getRoomEntityModel(username, r);
    }



    @GetMapping("/api/rooms/{id}/buildings") // -> admin ou men
    public EntityModel<Map<String, Object>> getRoomBuildingById(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable Long id) {
        if (!(checkIfAdmin(username)) && !(checkIfMine(username, id))) throw new AccessDeniedException("403 returned");

        Room r = null;
        try {
        r = roomrep.findById(id).orElseThrow();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return BuildingRestController.getBuildingEntityModel(username, r.getBuilding());
    }

    @GetMapping("/api/rooms/{id}/sensors") // -> admin ou meu
    public List<EntityModel<Map<String, Object>>> getRoomSensorsById(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable Long id) {
        if (!(checkIfAdmin(username)) && !(checkIfMine(username, id))) throw new AccessDeniedException("403 returned");
        Room r = null;
        try {
        r = roomrep.findById(id).orElseThrow();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (Sensor s : r.getSensors()) {
            l.add(SensorRestController.getSensorEntityModel(username, s));
        }
        //return BuildingRestController.getBuildingEntityModel(r.getBuilding());
        return l;
    }

    // -------- POST --------



    @PostMapping("/api/rooms/{id}/sensors") // -> admin ou meu
    public List<EntityModel<Map<String, Object>>> addSensorToBuilding(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable Long id, @RequestBody Sensor newsensor)
            throws BadAttributeValueExpException {
        if (!(checkIfAdmin(username)) && !(checkIfMine(username, id))) throw new AccessDeniedException("403 returned");

        Room r = null;
        try {
        r = roomrep.findById(id).orElseThrow();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (!(newsensor.getType().equals("CO2") || newsensor.getType().equals("PEOPLE_COUNTER") || newsensor.getType().equals("BODY_TEMPERATURE") )) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        newsensor.setRoom(r);

        r.addSensor(sensrep.save(newsensor));
        r = roomrep.save(r);

        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (Sensor s : r.getSensors()) {
            l.add(SensorRestController.getSensorEntityModel(username, s));
        }
        return l;
    }

    // ------ PUT (UPDATE) ------

    @PutMapping("/api/rooms/{id}")
    public EntityModel<Map<String, Object>> updateRoomById(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable long id, @RequestBody Room newroom) {
        if (!(checkIfAdmin(username)) && !(checkIfMine(username, id))) throw new AccessDeniedException("403 returned");
        
        Room r = null;
        try {
        r = roomrep.findById(id).orElseThrow();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // devia verificar se é null ou nao mas como sao 'ints'/'doubles' nao dá pq n sao objs.
        r.setRoom_number(newroom.getRoom_number());
        r.setFloorNumber(newroom.getFloorNumber());
        r.setMaxLevelCo2(newroom.getMaxOccupation());
        r.setMaxTemperature(newroom.getMaxTemperature());
        r.setMaxLevelCo2(newroom.getMaxLevelCo2());

        return getRoomEntityModel(username, roomrep.save(r));
    }

    // ------ DELETE ------

    @DeleteMapping("/api/rooms/{id}")
    public void deleteRoomById(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable long id) {
        if (!(checkIfAdmin(username)) && !(checkIfMine(username, id))) throw new AccessDeniedException("403 returned");
        
        Room r = null;
        try {
        r = roomrep.findById(id).orElseThrow();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        for (Sensor s : r.getSensors()) {
            sensdatarep.deleteAll(s.getSensorsData());
        }
        sensrep.deleteAll(r.getSensors());
        roomrep.delete(r);
    }



    @Autowired
    private SensorDataRepository sensdatarep;

    @Autowired
    private UserRepository userrep;
    

    public static EntityModel<Map<String, Object>> getRoomEntityModel(String username, Room r) {
        Map<String, Object> r_map = r.convertToMap();
        Long id = r.getId();
        return EntityModel.of(r_map,
            linkTo(methodOn(BuildingRestController.class).buildingById(username, r.getBuilding().getId())).withRel("building"),
            linkTo(methodOn(RoomRestController.class).getRoomSensorsById(username, id)).withRel("sensors"),
            linkTo(methodOn(RoomRestController.class).roomById(username, id)).withSelfRel() 
            );
        }
        

}
