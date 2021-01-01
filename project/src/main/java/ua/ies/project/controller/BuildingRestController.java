package ua.ies.project.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ua.ies.project.model.Building;
import ua.ies.project.model.User;
import ua.ies.project.repository.BuildingRepository;
import ua.ies.project.repository.RoleRepository;
import ua.ies.project.repository.UserRepository;

@RestController
public class BuildingRestController {
    @Autowired
    BuildingRepository buildrep;

    /* acho q estes n faz sentido ter (ou entao alterar para apenas mostrar os correspondentes!!)*/
    @GetMapping("/api/buildings")
    public List<Building> seeBuildings() {
        return buildrep.findAll();
    }

    @GetMapping("/api/buildings/id/{id}")
    public Optional<Building> buildingsById(@PathVariable Long id) {
        return buildrep.findById(id);
    }


    /* fim do comentario anterior */

    @PostMapping("/api/buildings")
    public Building newBuilding(@RequestBody Building newbuilding) {
        // preciso de saber como se vai buscar o user atual para o adicionar à lista
        return buildrep.save(newbuilding);
    }

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
/*
    @Override
    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(new HashSet<>(roleRepository.findAll()));
        userRepository.save(user);
    }
*/

}
