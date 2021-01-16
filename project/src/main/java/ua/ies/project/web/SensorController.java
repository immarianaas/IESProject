package ua.ies.project.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    /*
    Page<SensorData> p = sensdatarep.findBySensorId( s.getId(),
    PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "timestamp"))
    );
    sd = p.get().findFirst().get();
    */

    @GetMapping("/moreInfoSensor/{id}")
    public String moreInfoSensor(@PathVariable ( value = "id") long id, Model model, @CurrentSecurityContext(expression="authentication.name") String username,
            
    @RequestParam(required = false) Integer pageNo,
    @RequestParam(required = false) Integer pageSize
    ){
        
        // User ux = userRepository.findByUsername(username);
        model.addAttribute("sensorInfoID", id);
        Sensor s = sensorRepository.findById(id).get(); 

        // List<Object> allSensorsData = new ArrayList<>();

        if (pageNo == null) pageNo = 0;
        if (pageSize == null) pageSize = 50;

        Page<SensorData> p = sensorDataRepository.findBySensorId( s.getId(),
        PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "timestamp"))
        );

        model.addAttribute("sensor", s);
        model.addAttribute("sensorType", s.getType());
        if (s.getType().equals("CO2"))
            model.addAttribute("sensorMaxValue", s.getRoom().getMaxLevelCo2());
        else if (s.getType().equals("PEOPLE_COUNTER"))
            model.addAttribute("sensorMaxValue", s.getRoom().getMaxOccupation());
        else if (s.getType().equals("BODY_TEMPERATURE"))
            model.addAttribute("sensorMaxValue", s.getRoom().getMaxTemperature());
        /*
        if(s.getType().equals("CO2")){
            model.addAttribute("sensorType", "CO2");
            model.addAttribute("sensorMaxValue", s.getRoom().getMaxLevelCo2());
            allSensorsData.addAll(s.getSensorsData());
        }
        if(s.getType().equals("PEOPLE_COUNTER")){
            model.addAttribute("sensorType", "PEOPLE_COUNTER");
            model.addAttribute("sensorMaxValue", s.getRoom().getMaxOccupation());
            allSensorsData.addAll(s.getSensorsData());
        }
        if(s.getType().equals("BODY_TEMPERATURE")){
            model.addAttribute("sensorType", "BODY_TEMPERATURE");
            model.addAttribute("sensorMaxValue", s.getRoom().getMaxTemperature());
            allSensorsData.addAll(s.getSensorsData());            
        }
        
        List<Object> allSensorsData_first50Elements = new ArrayList<>();
        for(int i = 0; i<=50; i++){
            allSensorsData_first50Elements.add(allSensorsData.get(i));
            
        }
        */
        // model.addAttribute("sensorInfo", allSensorsData_first50Elements);
        model.addAttribute("sensorInfo", p);

        return "sensorInfo";
            }
    
   
  


  


}




