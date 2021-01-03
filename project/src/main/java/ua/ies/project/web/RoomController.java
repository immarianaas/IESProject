package ua.ies.project.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.ui.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ua.ies.project.model.Building;
import ua.ies.project.model.Room;
import ua.ies.project.model.Sensor;
import ua.ies.project.model.User;
import ua.ies.project.repository.BuildingRepository;
import ua.ies.project.repository.SensorDataRepository;
import ua.ies.project.repository.SensorRepository;
import ua.ies.project.repository.UserRepository;

@Controller
public class RoomController {

	@Autowired
	private BuildingRepository buildingRepository;

	@Autowired
	private UserRepository userRepository;
    	//----------- QUERIES--------------------

    /*
    @GetMapping("/searchRoomsAvailableByBuilding")
    public String getRoomsAvailable( @CurrentSecurityContext(expression="authentication.name") String username, Model model){
        System.out.println("OLSSSSSSSSSSSSSSSsssssA" + username);

        User u = userRepository.findByUsername(username);
        System.out.println(u.getUsername());
        Map<String, Set<Room>> mapbuildignsAndRooms = new HashMap<String, Set<Room>>();
        
        for(Building b :u.getBuildings()){
            System.out.println("OLSSSSSSSSSSSSSSSsssssA");


            mapbuildignsAndRooms.put(b.getBuildingName(), b.getRooms());


        }
        model.addAttribute("mapbuildignsAndRooms", mapbuildignsAndRooms);

        
        
    return "air_quality";
    }
    */


}



