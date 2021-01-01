package ua.ies.project.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ua.ies.project.model.Building;
import ua.ies.project.model.Room;
import ua.ies.project.model.User;
import ua.ies.project.repository.BuildingRepository;
import ua.ies.project.repository.RoleRepository;
import ua.ies.project.repository.RoomRepository;
import ua.ies.project.repository.UserRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;


@RestController
public class RoomRestController {
    @Autowired
    BuildingRepository buildrep;

    @Autowired
    RoomRepository roomrep;

    @GetMapping("/api/rooms/all") // to be removed, ou apenas permitir os admins
    public List<EntityModel<Map<String, Object>>> seeRooms() {
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (Room r : roomrep.findAll()) {
            l.add(getRoomEntityModel(r));
        }

        return l;
    }

    @GetMapping("/api/rooms/{id}") // TODO se for admin, permitir todos, se nao, apenas os correspondentes
    public EntityModel<Map<String, Object>> roomById(@PathVariable Long id) {
        Room r = roomrep.findById(id).orElseThrow();
        return getRoomEntityModel(r);
    }

/*
    @GetMapping("/api/rooms/{id}/users")
    public List<EntityModel<Map<String, Object>>> usersByBuilding(@PathVariable Long id) {
        Building b = buildrep.findById(id).orElseThrow();
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (User u : b.getUsers()) {
            l.add(UserRestController.getUserEntityModel(u));
        }
        return l;
    }
    */

/*
    @GetMapping("/api/buildings") // mostra apenas os do user atual!
    public List<EntityModel<Map<String, Object>>> seeBuildings(@CurrentSecurityContext(expression="authentication.name") String username) {
        User u = userrep.findByUsername(username);
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (Building b : u.getBuildings()) {
            l.add(getBuildingEntityModel(b));
        }
        return l;
    }
*/

    @GetMapping("/api/rooms/{id}/buildings") // TODO ver se ação é permitida ou nao
    public EntityModel<Map<String, Object>> getRoomBuildingById(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable Long id) {
        Room r = roomrep.findById(id).orElseThrow();
        return BuildingRestController.getBuildingEntityModel(r.getBuilding());
    }


    // -------- POST --------
    /*

    @PostMapping("/api/buildings")
    public EntityModel<Map<String, Object>> newBuilding(@CurrentSecurityContext(expression="authentication.name")
    String username, @RequestBody Building newbuilding) {
        User u = userrep.findByUsername(username);
        newbuilding = buildrep.save(newbuilding);
        u.addBuilding(newbuilding);
        u = userrep.save(u); // faz update!

        newbuilding.addUser(u);
        newbuilding = buildrep.save(newbuilding);
        
        //System.out.println(u);
        return getBuildingEntityModel(newbuilding);

    }

    @PostMapping("/api/buildings/{id}/rooms")
    public Set<Room> addRoomToBuilding(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable Long id, @RequestBody Room newroom) {
        Building b = buildrep.findById(id).get();
        newroom.setBuilding(b);
        
        b.addRoom(roomrep.save(newroom));
        b = buildrep.save(b);
        return b.getRooms();
    }

    */


    @Autowired
    private UserRepository userrep;
    

    public static EntityModel<Map<String, Object>> getRoomEntityModel(Room r) {
        Map<String, Object> r_map = r.convertToMap();
        Long id = r.getId();
        return EntityModel.of(r_map,
            linkTo(methodOn(BuildingRestController.class).buildingById(r.getBuilding().getId())).withRel("building"),
            // SENSORES -> linkTo(methodOn(BuildingRestController.class).buildingById(r.getBuilding().getId())).withRel("users"),
            linkTo(methodOn(RoomRestController.class).roomById(id)).withSelfRel() 
            );
        }
        

}
