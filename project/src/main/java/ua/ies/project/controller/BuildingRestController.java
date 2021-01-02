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
public class BuildingRestController {
    @Autowired
    BuildingRepository buildrep;

    @Autowired
    RoomRepository roomrep;

    /*
     * acho q estes n faz sentido ter (ou entao alterar para apenas mostrar os
     * correspondentes!!)
     */
    @GetMapping("/api/buildings/all") // to be removed, ou apenas permitir os admins
    public List<EntityModel<Map<String, Object>>> seeBuildings() {
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (Building b : buildrep.findAll()) {
            l.add(getBuildingEntityModel(b));
        }

        return l;
    }

    @GetMapping("/api/buildings/{id}") // TODO se for admin, permitir todos, se nao, apenas os correspondentes
    public EntityModel<Map<String, Object>> buildingById(@PathVariable Long id) {
        Building b = buildrep.findById(id).orElseThrow();
        return getBuildingEntityModel(b);
    }

    public static EntityModel<Map<String, Object>> getBuildingEntityModel(Building b) {
        Map<String, Object> b_map = b.convertToMap();
        Long id = b.getId();
        return EntityModel.of(b_map,
            linkTo(methodOn(BuildingRestController.class).usersByBuilding(id)).withRel("users"),
            linkTo(methodOn(BuildingRestController.class).roomsByBuilding(id)).withRel("rooms"),
            linkTo(methodOn(BuildingRestController.class).buildingById(id)).withSelfRel() 
            );
        }

    @GetMapping("/api/buildings/{id}/users")
    public List<EntityModel<Map<String, Object>>> usersByBuilding(@PathVariable Long id) {
        Building b = buildrep.findById(id).orElseThrow();
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (User u : b.getUsers()) {
            l.add(UserRestController.getUserEntityModel(u));
        }
        return l;
    }

    @GetMapping("/api/buildings/{id}/rooms")
    public List<EntityModel<Map<String, Object>>> roomsByBuilding(@PathVariable Long id) {
        Building b = buildrep.findById(id).orElseThrow();
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (Room r : b.getRooms()) {
            l.add(RoomRestController.getRoomEntityModel(r));
        }
        return l;
    }


    @GetMapping("/api/buildings") // mostra apenas os do user atual!!
    public List<EntityModel<Map<String, Object>>> seeBuildings(@CurrentSecurityContext(expression="authentication.name") String username) {
        User u = userrep.findByUsername(username);
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (Building b : u.getBuildings()) {
            l.add(getBuildingEntityModel(b));
        }
        return l;
    }

    /*
    @GetMapping("/api/buildings/{id}/rooms") // TODO ver se ação é permitida ou nao
    public Set<Room> getBuildingRooms(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable Long id) {
        Optional<Building> b_opt = buildrep.findById(id);
        Building b = null;
        if (b_opt.isPresent()) {
            b = b_opt.get();
        }
        return b.getRooms();
    }
    */


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
    public EntityModel<Map<String, Object>> addRoomToBuilding(
            @CurrentSecurityContext(expression="authentication.name") String username, 
            @PathVariable Long id, 
            @RequestBody Room newroom) {
        Building b = buildrep.findById(id).get();
        newroom.setBuilding(b);
        Room nr = roomrep.save(newroom);
        b.addRoom(nr);
        b = buildrep.save(b);

        return RoomRestController.getRoomEntityModel(nr);
    }

    @PostMapping("/api/buildings/{id}/users")
    public List<EntityModel<Map<String, Object>>> addUserToBuilding(
        @CurrentSecurityContext(expression="authentication.name") String username, 
        @PathVariable Long id, 
        @RequestBody Map<String, Object> rec ) {
            Building b = buildrep.getOne(id);

            User u = null;
            if (rec.keySet().contains("id")) {
                u = userrep.getOne((Long) rec.get("id"));
            } else if (rec.keySet().contains("username")) {
                u = userrep.findByUsername((String) rec.get("username"));
            }

            if (u == null || b == null) return null; // TODO meter um erro qq

            u.addBuilding(b);
            b.addUser(userrep.save(u));
            b = buildrep.save(b);

            List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
            for (User us : b.getUsers()) {
                l.add(UserRestController.getUserEntityModel(us));
            }
            return l;
        }
    



    @Autowired
    private UserRepository userrep;
    
/*
    @Override
    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(new HashSet<>(roleRepository.findAll()));
        userRepository.save(user);
    }
*/

}
