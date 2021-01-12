package ua.ies.project.controller;

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

import ua.ies.project.repository.RoleRepository;
import ua.ies.project.repository.UserRepository;
import ua.ies.project.model.*;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;


@RestController
public class RoleRestController {
    
    @Autowired
    private RoleRepository rolerep;

    @Autowired
    private UserRepository userrep;


    public boolean checkIfAdmin(String uname) {
        User u = userrep.findByUsername(uname);
        if (u.getRoles() != null)
            for (Role r : u.getRoles()) 
                if (r.getName().equals("admin")) return true;
        return false;
    }


    @GetMapping("/api/roles/all") // -> todos
    public List<EntityModel<Map<String, Object>>> seeRoles(@CurrentSecurityContext(expression="authentication.name") String username) {
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (Role r:  rolerep.findAll()) {
            l.add(getRoleEntityModel(username, r));
        }
        return l;
    }

    @GetMapping("/api/roles/{id}") // -> todos
    public EntityModel<Map<String, Object>> roleById(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable Long id) {
        Role r = null;
        try {
            r = rolerep.findById(id)
            .orElseThrow();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return getRoleEntityModel(username, r);
    }

    @GetMapping("/api/roles/{id}/users") // -> todos
    public List<EntityModel<Map<String, Object>>> usersInRoleById(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable Long id) {
        Role r = null;
        try {
            r = rolerep.findById(id)
            .orElseThrow();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (User u:  r.getUsers()) {
            l.add(UserRestController.getUserEntityModel(username, u));
        }
        return l;    
    }

    @PostMapping("/api/roles") // -> admin
    public List<EntityModel<Map<String, Object>>> addRole(@CurrentSecurityContext(expression="authentication.name") String username, @RequestBody Role role) {
        if (!checkIfAdmin(username)) throw new AccessDeniedException("403 returned");
        
        rolerep.save(role);
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (Role r:  rolerep.findAll()) {
            l.add(getRoleEntityModel(username, r));
        }
        return l;    
    }

    @PostMapping("/api/roles/{id}/users")
    public List<EntityModel<Map<String, Object>>> addUserToRole(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable Long id, @RequestBody Map<String, Object> map) {
        if (!checkIfAdmin(username)) throw new AccessDeniedException("403 returned");

        Role r = null;
        try {
            r = rolerep.findById(id)
            .orElseThrow();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        User u = null;
        if (map.containsKey("id")) {
            u = userrep.getOne((Long) map.get("id"));
        } else if (map.containsKey("username")) {
            u = userrep.findByUsername((String) map.get("username"));
        }
        if (r == null || u == null) { System.err.println("\t\terror!"); return null;} // TODO something else here

        r.addUser(u);
        r = rolerep.save(r);
        u.addRole(r);
        u = userrep.save(u);

        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (User us: r.getUsers()) {
            l.add(UserRestController.getUserEntityModel(username, us));
        }
        return l;    
    }


    // ------- PUT -------
    @PutMapping("/api/roles/{id}")
    public EntityModel<Map<String, Object>> updateRoleById(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable long id, @RequestBody Role newrole) {
        if (!checkIfAdmin(username)) throw new AccessDeniedException("403 returned");
        
        Role r = null;
        try {
            r = rolerep.findById(id)
            .orElseThrow();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (newrole.getName() != null) r.setName(newrole.getName());
        return getRoleEntityModel(username, rolerep.save(r));
    }


    // ------- DELETE ------
    @DeleteMapping("/api/roles/{id}")
    public void deleteRoleById(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable long id) {
        if (!checkIfAdmin(username)) throw new AccessDeniedException("403 returned");
        
        Role r = null;
        try {
            r = rolerep.findById(id)
            .orElseThrow();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        for (User u : r.getUsers()) {
            u.getRoles().remove(r);
            userrep.save(u);
        }
        rolerep.delete(r);
    }

    @DeleteMapping("/api/roles/{id}/users/{userid}")
    public void removeUserFromRole(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable long id, @PathVariable long userid) {
        if (!checkIfAdmin(username)) throw new AccessDeniedException("403 returned");
        
        Role r = null;
        User u = null;
        try {
            r = rolerep.findById(id)
            .orElseThrow();
            u = userrep.findById(userid).get();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (r == null || u == null) return;
        u.getRoles().remove(r);
        u = userrep.save(u);

        r.getUsers().remove(u);
        rolerep.save(r);
    }


    public static EntityModel<Map<String, Object>> getRoleEntityModel(String username, Role r) {
        Long id = r.getId();
        return EntityModel.of(r.convertToMap(),
                linkTo(methodOn(RoleRestController.class).usersInRoleById(username, id)).withRel("users"),
                linkTo(methodOn(RoleRestController.class).roleById(username, id)).withSelfRel() 
        );
    }

}
