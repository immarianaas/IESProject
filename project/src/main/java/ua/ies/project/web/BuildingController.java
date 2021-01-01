package ua.ies.project.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import ua.ies.project.model.Building;
import ua.ies.project.repository.BuildingRepository;
import ua.ies.project.repository.UserRepository;
import java.util.Optional;

@Controller
public class BuildingController {

	@Autowired
	private BuildingRepository buildingRepository;

	@Autowired
	private UserRepository userRepository;
	
	//PARECE SER MAIS O dashboard do webController
	@GetMapping("/allBuildings")
	public String viewHomePage(Model model) {
		
		//load buildings
		List<Building> listBuildings = buildingRepository.findAll();
		model.addAttribute("listBuildings", listBuildings);


		return "dashboard";
	}
	
	
	
	@GetMapping("/newBuildingForm")
	public String shownewBuildingForm(Model model) {
		Building building = new Building();
		model.addAttribute("building", building);
		return "newBuilding";
	}
	
	@PostMapping("/saveBuilding")
	public String saveBuilding(@ModelAttribute("building") Building building,  Model model, @CurrentSecurityContext(expression="authentication.name") String username) {
		//user authenticated
		//User user = userService.findByUsername(username);

		

		//load all buildings
		buildingRepository.save(building);
		
		List<Building> listBuildings = buildingRepository.findAll();
		
		model.addAttribute("listBuildings", listBuildings);
		
		return "dashboard";
	}
	
	public Building getBuildingById(long id) {
		Optional<Building> optional = buildingRepository.findById(id);
		
		Building building = null;
		
		if (optional.isPresent()) {
			building = optional.get();
		} else {
			throw new RuntimeException(" Building not found ->  " + id);
		}
		
		return building;
	}

	@GetMapping("/showFormForUpdate/{id}")
	public String showFormForUpdate(@PathVariable ( value = "id") long id, Model model) {
		Building building = getBuildingById(id);
		model.addAttribute("building", building);
		List<Building> listBuildings = buildingRepository.findAll();
		
		model.addAttribute("listBuildings", listBuildings);
		
		return "updateBuilding";
	}
	
	@GetMapping("/deleteBuilding/{id}")
	public String deleteBuilding(@PathVariable (value = "id") long id,  Model model) {
		this.buildingRepository.deleteById(id);

		List<Building> listBuildings = buildingRepository.findAll();
		model.addAttribute("listBuildings", listBuildings);
		
		return "dashboard";
	}

	
}