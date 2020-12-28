package ua.ies.project.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ua.ies.project.model.Building;
import ua.ies.project.service.BuildingService;

@Controller
public class BuildingController {

	@Autowired
	private BuildingService buildingService;
	
	//TODO: Quando se grava um building ao fazer save deve estar th:href="@{/allBuildings}" e n como esta. 
	//quando se carrega na dash tmb n atualiza
	//apenas problemas de redirecionamento pq de resto funciona
	
	@GetMapping("/allBuildings")
	public String viewHomePage(Model model) {
		return findPaginated(1, "buildingName", "asc", model);		
	}
	
	
	
	@GetMapping("/newBuildingForm")
	public String shownewBuildingForm(Model model) {
		Building building = new Building();
		model.addAttribute("building", building);
		return "newBuilding";
	}
	
	@PostMapping("/saveBuilding")
	public String saveBuilding(@ModelAttribute("building") Building building) {
		buildingService.saveBuilding(building);
		return "redirect:/dashboard";
	}
	
	@GetMapping("/showFormForUpdate/{id}")
	public String showFormForUpdate(@PathVariable ( value = "id") long id, Model model) {
		Building building = buildingService.getBuildingById(id);
		model.addAttribute("building", building);
		return "updateBuilding";
	}
	
	@GetMapping("/deleteBuilding/{id}")
	public String deleteBuilding(@PathVariable (value = "id") long id) {
		this.buildingService.deleteBuildingById(id);
		return "redirect:/dashboard";
	}
	
	
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
}