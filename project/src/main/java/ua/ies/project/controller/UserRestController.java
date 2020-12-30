package ua.ies.project.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ua.ies.project.model.User;
import ua.ies.project.repository.UserRepository;

@RestController
public class UserRestController {
    @Autowired
    UserRepository userrep;

    @GetMapping("/users")
    public List<User> seeUsers() {
        return userrep.findAll();
    }

    @GetMapping("/users/id/{id}")
    public Optional<User> userById(@PathVariable Long id) {
        return userrep.findById(id);
    }

    @GetMapping("/users/username/{uname}")
    public User userByUname(@PathVariable String uname) {
        return userrep.findByUsername(uname);
    }

    @PostMapping("/users") // not tested pq n consigo entrar c o postman por causa do login
    public User newUser(@RequestBody User newuser) {
        return userrep.save(newuser);
    }
}
