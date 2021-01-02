package ua.ies.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/api/roles/all")
    public List<EntityModel<Map<String, Object>>> seeRoles() {
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (Role r:  rolerep.findAll()) {
            l.add(getRoleEntityModel(r));
        }
        return l;
    }

    @GetMapping("/api/roles/{id}")
    public EntityModel<Map<String, Object>> roleById(@PathVariable Long id) {
        Role r = rolerep.findById(id)
            .orElseThrow();
        return getRoleEntityModel(r);
        }

    

    public static EntityModel<Map<String, Object>> getRoleEntityModel(Role r) {
        Long id = r.getId();
        return EntityModel.of(r.convertToMap(),
                // linkTo(methodOn(UserRestController.class).usersByRole(role_id)).withRel("users"),
                linkTo(methodOn(RoleRestController.class).roleById(id)).withSelfRel() 
        );
    }

}
