package ua.ies.project.controller;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.security.access.AccessDeniedException;

import ua.ies.project.model.Building;
import ua.ies.project.model.Role;
import ua.ies.project.model.User;
import ua.ies.project.repository.BuildingRepository;
import ua.ies.project.repository.RoleRepository;
import ua.ies.project.repository.UserRepository;

@RestController
public class UserRestController {
    @Autowired
    private UserRepository userrep;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public boolean checkIfAdmin(String uname) {
        User u = userrep.findByUsername(uname);
        if (u.getRoles() != null)
            for (Role r : u.getRoles()) 
                if (r.getName().equals("admin")) return true;
        return false;
    }


    public boolean checkIfMine(String uname, long user_id) {
        User u = userrep.findByUsername(uname);
        return u.getId().equals(user_id);
    }

    @GetMapping("/api/users/all") // -> todos
    public List<EntityModel<Map<String, Object>>> seeUsers(@CurrentSecurityContext(expression="authentication.name") String username) {
        //if (!checkIfAdmin(username)) throw new AccessDeniedException("403 returned");
        
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (User u:  userrep.findAll()) {
            l.add(getUserEntityModel(username, u));
        }
        return l;
    }

    
    @GetMapping("/api/users/{id}") // -> todos
    public EntityModel<Map<String, Object>> userById(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable Long id) {
        User u = null;
        try {
            u = userrep.findById(id).orElseThrow();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return getUserEntityModel(username, u);
        }


    @GetMapping("/api/users/{id}/roles") // -> todos
    public List<EntityModel<Map<String, Object>>> rolesByUserId(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable Long id) {
        User u = null;
        try {
        u = userrep.findById(id).orElseThrow();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (Role r : u.getRoles()) {
            l.add(RoleRestController.getRoleEntityModel(username, r));
        }
        return l;
    }


    @GetMapping("/api/users/{id}/buildings") // -> meu ou admin
    public List<EntityModel<Map<String, Object>>> buildingsByUser(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable Long id) {
        if (!(checkIfAdmin(username)) && !(checkIfMine(username, id))) throw new AccessDeniedException("403 returned");
        User u = null;
        try {
        u = userrep.findById(id).orElseThrow();
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (Building b : u.getBuildings()) {
            l.add(BuildingRestController.getBuildingEntityModel(username, b));
        }
        return l;
    }


    @GetMapping("/api/users/username/{uname}") // -> todos
    public EntityModel<Map<String, Object>> userByUname(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable String uname) {
        User u = userrep.findByUsername(uname);
        if (u == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return getUserEntityModel(username, u);
    }


    @PostMapping("/api/users") // -> todos
    public EntityModel<Map<String, Object>> newUser(@CurrentSecurityContext(expression="authentication.name") String username, @RequestBody User newuser) {
        newuser.setPassword(bCryptPasswordEncoder.encode(newuser.getPassword()));
        //newuser.setRoles(new HashSet<Role>(roleRepository.findById((long) 1)));
        newuser.addRole(roleRepository.getOne((long)2));
        newuser = userrep.save(newuser);
        return getUserEntityModel(username, newuser);
    }


    // ------ PUT (UPDATE) ------ 

    @PutMapping("/api/users/{id}")
    public EntityModel<Map<String, Object>> updateUserById(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable long id, @RequestBody User newuser) {
        if (!(checkIfAdmin(username)) && !(checkIfMine(username, id))) throw new AccessDeniedException("403 returned");
        
        User u = null;
        try {
            u = userrep.findById(id).get();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (newuser.getUsername() != null) u.setUsername(newuser.getUsername());
        if (newuser.getPassword() != null) u.setPassword(bCryptPasswordEncoder.encode(newuser.getPassword()));

        return getUserEntityModel(username, userrep.save(u));
    }


    // ------ DELETE ------ (ainda n tem permissoes certas (da todos))

    @DeleteMapping("/api/users/{id}")
    public void deleteUserById(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable long id, @RequestBody User newuser) {
        if (!(checkIfAdmin(username)) && !(checkIfMine(username, id))) throw new AccessDeniedException("403 returned");
        
        User u = null;
        try {
            u = userrep.findById(id).get();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        for (Building b : u.getBuildings()) {
            b.getUsers().remove(u);
            buildrep.save(b);
        }
        userrep.delete(u);
    }


    @Autowired
    private BuildingRepository buildrep;


    public static EntityModel<Map<String, Object>> getUserEntityModel(String username, User u) {
    Map<String, Object> u_map = u.convertToMap();
    Long id = u.getId();
    return EntityModel.of(u_map,
        linkTo(methodOn(UserRestController.class).buildingsByUser(username, id)).withRel("buildings"),
        linkTo(methodOn(UserRestController.class).rolesByUserId(username, id)).withRel("roles"),
        linkTo(methodOn(UserRestController.class).userById(username, id)).withSelfRel() 
        );
    }


}
