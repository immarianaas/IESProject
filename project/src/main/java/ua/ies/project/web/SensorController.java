package ua.ies.project.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.ui.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ua.ies.project.model.Room;
import ua.ies.project.model.Sensor;
import ua.ies.project.repository.SensorDataRepository;
import ua.ies.project.repository.SensorRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Configuration;


import ua.ies.project.model.BodyTemperature;
import ua.ies.project.model.Co2;
import ua.ies.project.model.PeopleCounter;
import ua.ies.project.model.User;
import ua.ies.project.repository.BodyTemperatureRepository;
import ua.ies.project.repository.BuildingRepository;
import ua.ies.project.repository.Co2Repository;
import ua.ies.project.repository.PeopleCounterRepository;
import ua.ies.project.repository.UserRepository;
import ua.ies.project.model.Building;


@Controller
public class SensorController {

    @Autowired
    private SensorDataRepository sensorDataRepository;
   
    @Autowired
    private SensorRepository sensorRepository;
    
        
    @Autowired
	private UserRepository userRepository;
	//----------- QUERIES--------------------

    @GetMapping("/searchSensorTypeByRoom")
    public String getSensorDataByRoom(@RequestParam(value="searchTerm", required = false) Long room_id,  Model model, @CurrentSecurityContext(expression="authentication.name") String username){

        List<Sensor> sensorL = sensorRepository.findAll();
        Iterator<Sensor> s = sensorL.iterator();

        List<Sensor> sensorList = new ArrayList<Sensor>(); 

        while (s.hasNext()) {
            Sensor sensor = s.next();
            Room r = sensor.getRoom();
            if(r.getId() == room_id){
                sensorList.add(sensor);
            }
        }
		
        model.addAttribute("sensorList", sensorList);
            //buildings and rooms of user
            User u = userRepository.findByUsername(username);
            System.out.println(u.getUsername());
            Map<String, Set<Room>> mapbuildingsAndRooms = new HashMap<String, Set<Room>>();
            
            for(Building b :u.getBuildings()){
                for(Room r : b.getRooms()){
                    if(r.getFloorNumber() != 0 && r.getRoom_number() != 0){
                        mapbuildingsAndRooms.put(b.getBuildingName(), b.getRooms());


                    }

                }
            }
            model.addAttribute("mapbuildingsAndRooms", mapbuildingsAndRooms);
		
    return "dashboard";
    }

  


}




