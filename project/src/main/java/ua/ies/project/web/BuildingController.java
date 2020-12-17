package ua.ies.project.web;

import com.it.api.exception.ResourceNotFoundException;
import com.it.api.model.Department;

@RestController
public class UserController {
    @Autowired
    private BuildingRepository buildingRepository;

    
    @GetMapping("/buildings")
    public List<Building> getAllBuildings() {
        return buildingRepository.findAll();
    }


}

