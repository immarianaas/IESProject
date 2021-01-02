package ua.ies.project.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.support.Repositories;
import org.springframework.hateoas.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import ua.ies.project.model.Building;
import ua.ies.project.model.Role;
import ua.ies.project.model.User;
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

    /* acho q estes n faz sentido ter */
    @GetMapping("/api/users")
    public List<EntityModel<Map<String, Object>>> seeUsers() {
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (User u:  userrep.findAll()) {
            l.add(getUserEntityModel(u));
        }
        return l;
    }

    /*
    @GetMapping("/employees/{id}")
EntityModel<Employee> one(@PathVariable Long id) {

  Employee employee = repository.findById(id) //
      .orElseThrow(() -> new EmployeeNotFoundException(id));

  return EntityModel.of(employee, //
      linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel(),
      linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
}
*/
    
    @GetMapping("/api/users/{id}")
    public EntityModel<Map<String, Object>> userById(@PathVariable Long id) {
        User u = userrep.findById(id)
            .orElseThrow();
        return getUserEntityModel(u);
        }


    @GetMapping("/api/users/{id}/roles") 
    public List<EntityModel<Map<String, Object>>> rolesByUserId(@PathVariable Long id) {
        User u = userrep.findById(id).orElseThrow();
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (Role r : u.getRoles()) {
            l.add(RoleRestController.getRoleEntityModel(r));
        }
        return l;
    }

    @GetMapping("/api/users/{id}/buildings")
    public List<EntityModel<Map<String, Object>>> buildingsByUser(@PathVariable Long id) {
        User u = userrep.findById(id).orElseThrow();
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
        for (Building b : u.getBuildings()) {
            l.add(BuildingRestController.getBuildingEntityModel(b));
        }
        return l;
    }
    /*
    @GetMapping("/api/users/{id}")
    public Optional<User> userById(@PathVariable Long id) {
        return userrep.findById(id);
    }
    */

    @GetMapping("/api/users/username/{uname}")
    public EntityModel<Map<String, Object>> userByUname(@PathVariable String uname) {
        User u = userrep.findByUsername(uname);
        return getUserEntityModel(u);
    }

    /* acho q estes n faz sentido ter - ou so pro admin*/

    @PostMapping("/api/users")
    public EntityModel<Map<String, Object>> newUser(@RequestBody User newuser) {
        newuser.setPassword(bCryptPasswordEncoder.encode(newuser.getPassword()));
        newuser.setRoles(new HashSet<>(roleRepository.findAll()));
        newuser = userrep.save(newuser);
        return getUserEntityModel(newuser);
    }

/*
    @Override
    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(new HashSet<>(roleRepository.findAll()));
        userRepository.save(user);
    }
*/

public static EntityModel<Map<String, Object>> getUserEntityModel(User u) {
    Map<String, Object> u_map = u.convertToMap();
    Long id = u.getId();
    return EntityModel.of(u_map,
        linkTo(methodOn(UserRestController.class).buildingsByUser(id)).withRel("buildings"),
        linkTo(methodOn(UserRestController.class).rolesByUserId(id)).withRel("roles"),
        linkTo(methodOn(UserRestController.class).userById(id)).withSelfRel() 
        );
    }


}
