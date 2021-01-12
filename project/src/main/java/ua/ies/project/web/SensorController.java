package ua.ies.project.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import ua.ies.project.model.Room;
import ua.ies.project.model.Sensor;
import ua.ies.project.model.SensorData;
import ua.ies.project.repository.SensorDataRepository;
import ua.ies.project.repository.SensorRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ua.ies.project.model.User;
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
    public boolean checkIfMine(String uname, long data_id) {
        SensorData sd = sensorDataRepository.findById(data_id).get();
        for (User u : sd.getSensor().getRoom().getBuilding().getUsers()) {
            if (u.getUsername().equals(uname)) return true;
        }
        return false;
    }
    

    @GetMapping("/searchSensorTypeByRoom")
    public String getSensorDataByRoom(@RequestParam(value="searchTerm", required = false) Long room_id,  Model model, @CurrentSecurityContext(expression="authentication.name") String username, @PathVariable Long id){
        if (!checkIfMine(username,id)) throw new AccessDeniedException("403 returned");

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


    @GetMapping("/moreInfoSensor/{id}")
    public String moreInfoSensor(@PathVariable ( value = "id") long id, Model model, @CurrentSecurityContext(expression="authentication.name") String username){
        User ux = userRepository.findByUsername(username);
			
			Set<Building> buildings =  ux.getBuildings();
			Set<Room> allRooms = new HashSet<>();
			for(Building b : buildings){
					Set<Room> rooms = b.getRooms();
					allRooms.addAll(rooms);
				
			}
			Set<SensorData> allSensorsData = new HashSet<>();
			Set<Room> rooms = allRooms;
			if(rooms.size() != 0){
				for(Room r : rooms){
					Set<Sensor> sensors = r.getSensors();
					if(sensors.size() != 0){
						for(Sensor s : sensors){
                                if(s.getId() == id){
                                    allSensorsData.addAll(s.getSensorsData());
                                }
						    }
					    }
				    }
                }
            

            model.addAttribute("sensorInfo", allSensorsData);



        return "sensorInfo";
            }
    
   
  


  


}




