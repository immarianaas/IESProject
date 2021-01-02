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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ua.ies.project.model.Building;
import ua.ies.project.model.Role;
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

    public boolean checkIfAdmin(String uname) {
        User u = userrep.findByUsername(uname);
        if (u.getRoles() != null)
            for (Role r : u.getRoles()) 
                if (r.getName().equals("admin")) return true;
        return false;
    }


    public boolean checkIfMine(String uname, long building_id) {
        //User u = userrep.findByUsername(uname);
        Building b = buildrep.findById(building_id).get();
        for (User u : b.getUsers()) {
            if (u.getUsername().equals(uname)) return true;
        }
        return false;
    }


    @GetMapping("/api/buildings/all") // -> admin
    public List<EntityModel<Map<String, Object>>> seeAllBuildings(@CurrentSecurityContext(expression="authentication.name") String username) {
        if (!checkIfAdmin(username)) throw new AccessDeniedException("403 returned");

        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (Building b : buildrep.findAll()) {
            l.add(getBuildingEntityModel(username, b));
        }

        return l;
    }

    @GetMapping("/api/buildings/{id}") // -> admin ou meu
    public EntityModel<Map<String, Object>> buildingById(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable Long id) {
        if (!(checkIfAdmin(username)) && !(checkIfMine(username, id))) throw new AccessDeniedException("403 returned");

        Building b = buildrep.findById(id).orElseThrow();
        return getBuildingEntityModel(username, b);
    }

    public static EntityModel<Map<String, Object>> getBuildingEntityModel(String username, Building b) {
        Map<String, Object> b_map = b.convertToMap();
        Long id = b.getId();
        return EntityModel.of(b_map,
            linkTo(methodOn(BuildingRestController.class).usersByBuilding(username, id)).withRel("users"),
            linkTo(methodOn(BuildingRestController.class).roomsByBuilding(username, id)).withRel("rooms"),
            linkTo(methodOn(BuildingRestController.class).buildingById(username, id)).withSelfRel() 
            );
        }


    @GetMapping("/api/buildings/{id}/users") // -> admin ou meu
    public List<EntityModel<Map<String, Object>>> usersByBuilding(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable Long id) {
        if (!(checkIfAdmin(username)) && !(checkIfMine(username, id))) throw new AccessDeniedException("403 returned");
        
        Building b = buildrep.findById(id).orElseThrow();
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (User u : b.getUsers()) {
            l.add(UserRestController.getUserEntityModel(username, u));
        }
        return l;
    }

    @GetMapping("/api/buildings/{id}/rooms") // -> admin ou meu
    public List<EntityModel<Map<String, Object>>> roomsByBuilding(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable Long id) {
        if (!(checkIfAdmin(username)) && !(checkIfMine(username, id))) throw new AccessDeniedException("403 returned");

        Building b = buildrep.findById(id).orElseThrow();
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (Room r : b.getRooms()) {
            l.add(RoomRestController.getRoomEntityModel(username, r));
        }
        return l;
    }


    @GetMapping("/api/buildings") // -> todos (mostra apenas os meus)
    public List<EntityModel<Map<String, Object>>> seeBuildings(@CurrentSecurityContext(expression="authentication.name") String username) {
        User u = userrep.findByUsername(username);
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (Building b : u.getBuildings()) {
            l.add(getBuildingEntityModel(username, b));
        }
        return l;
    }



    @PostMapping("/api/buildings") // -> todos (cria um associado a si/mim)
    public EntityModel<Map<String, Object>> newBuilding(@CurrentSecurityContext(expression="authentication.name")
    String username, @RequestBody Building newbuilding) {
        User u = userrep.findByUsername(username);
        newbuilding = buildrep.save(newbuilding);
        u.addBuilding(newbuilding);
        u = userrep.save(u); // faz update!

        newbuilding.addUser(u);
        newbuilding = buildrep.save(newbuilding);
        
        //System.out.println(u);
        return getBuildingEntityModel(username, newbuilding);

    }

    @PostMapping("/api/buildings/{id}/rooms") // -> admin ou meu (adicionar salas aquele edf)
    public EntityModel<Map<String, Object>> addRoomToBuilding(
            @CurrentSecurityContext(expression="authentication.name") String username, 
            @PathVariable Long id, 
            @RequestBody Room newroom) {
        if (!(checkIfAdmin(username)) && !(checkIfMine(username, id))) throw new AccessDeniedException("403 returned");

        Building b = buildrep.findById(id).get();
        newroom.setBuilding(b);
        Room nr = roomrep.save(newroom);
        b.addRoom(nr);
        b = buildrep.save(b);

        return RoomRestController.getRoomEntityModel(username, nr);
    }

    @PostMapping("/api/buildings/{id}/users") // -> admin ou eu (adicionar users aquele edf)
    public List<EntityModel<Map<String, Object>>> addUserToBuilding(
        @CurrentSecurityContext(expression="authentication.name") String username, 
        @PathVariable Long id, 
        @RequestBody Map<String, Object> rec ) {
            if (!(checkIfAdmin(username)) && !(checkIfMine(username, id))) throw new AccessDeniedException("403 returned");

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
                l.add(UserRestController.getUserEntityModel(username, us));
            }
            return l;
        }


    @Autowired
    private UserRepository userrep;


}
