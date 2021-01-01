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
import ua.ies.project.model.User;
import ua.ies.project.service.BuildingService;
import ua.ies.project.service.UserService;

@Controller
public class BuildingController {

	@Autowired
	private BuildingService buildingService;

	@Autowired
	private UserService userService;
	
	//PARECE SER MAIS O dashboard do webController
	@GetMapping("/allBuildings")
	public String viewHomePage(Model model) {
		
		//load buildings
		List<Building> listBuildings = buildingService.getAllBuildings();
		model.addAttribute("listBuildings", listBuildings);


		return "dashboard";
		//return findPaginated(1, "buildingName", "asc", model);		
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
		User user = userService.findByUsername(username);

		

		//load all buildings
		buildingService.saveBuilding(building);
		
		List<Building> listBuildings = buildingService.getAllBuildings();
		
		model.addAttribute("listBuildings", listBuildings);
		
		return "dashboard";
	}
	
	@GetMapping("/showFormForUpdate/{id}")
	public String showFormForUpdate(@PathVariable ( value = "id") long id, Model model) {
		Building building = buildingService.getBuildingById(id);
		model.addAttribute("building", building);
		List<Building> listBuildings = buildingService.getAllBuildings();
		
		model.addAttribute("listBuildings", listBuildings);
		
		return "updateBuilding";
	}
	
	@GetMapping("/deleteBuilding/{id}")
	public String deleteBuilding(@PathVariable (value = "id") long id,  Model model) {
		this.buildingService.deleteBuildingById(id);

		List<Building> listBuildings = buildingService.getAllBuildings();
		model.addAttribute("listBuildings", listBuildings);
		
		return "dashboard";
	}
	
	/*
	
	@GetMapping("/page/{pageNo}")
	public String findPaginated(@PathVariable (value = "pageNo") int pageNo, 
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir,
			Model model) {
		int pageSize = 4;
		
		Page<Building> page = buildingService.findPaginated(pageNo, pageSize, sortField, sortDir);
		List<Building> listBuildings = page.getContent();
		
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		model.addAttribute("listBuildings", listBuildings);
		return "dashboard";
	}
	*/
}