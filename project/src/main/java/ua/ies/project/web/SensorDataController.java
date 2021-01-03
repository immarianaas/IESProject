package ua.ies.project.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import ua.ies.project.model.Building;
import ua.ies.project.model.Co2;
import ua.ies.project.model.Room;
import ua.ies.project.model.Sensor;
import ua.ies.project.model.SensorData;
import ua.ies.project.model.User;
import ua.ies.project.repository.Co2Repository;
import ua.ies.project.repository.SensorDataRepository;
import ua.ies.project.repository.SensorRepository;
import ua.ies.project.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class SensorDataController {

    @Autowired
    private SensorDataRepository sensorDataRepository;
   
    @Autowired
    private SensorRepository sensorRepository;
    
        
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
	private Co2Repository co2Repository;
	//----------- QUERIES--------------------
    public boolean checkIfMine(String uname, long data_id) {
        SensorData sd = sensorDataRepository.findById(data_id).get();
        for (User u : sd.getSensor().getRoom().getBuilding().getUsers()) {
            if (u.getUsername().equals(uname)) return true;
        }
        return false;
    }
    

    @GetMapping("/sensorCo2AllData")
    public String getSensorDataByRoom( Model model, @CurrentSecurityContext(expression="authentication.name") String username){
        System.out.println("  WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWw");
        //if (!checkIfMine(username,id)) throw new AccessDeniedException("403 returned");

        
        User u = userRepository.findByUsername(username);
        Set<Building> buildings =  u.getBuildings();
        System.out.println("-#############BUILDINGS   " + buildings.size());


        Set<SensorData> allSensorsData = new HashSet<>();
        for(Building b : buildings){

            Set<Room> rooms = b.getRooms();
            if(rooms.size() != 0){
                for(Room r : rooms){
                    System.out.println("DDD");
                    
                    Set<Sensor> sensors = r.getSensors();

                    if(sensors.size() != 0){
                        for(Sensor s : sensors){
                            System.out.println("TYEP  " + s.getType());
        
                            if(s.getType().equals("CO2")){
                                System.out.println("TRUEE");   
                        }

                    }
                }
            }

        }
        System.out.println("-#############   " + allSensorsData.size());

        Map<Integer, ArrayList<String>> graphData = new HashMap<>();
        for (SensorData sd : allSensorsData) {
            Co2 co2Object= co2Repository.findById(sd.getId()).get();

            if(sd.getWarn()){
                ArrayList<String> column = new ArrayList<String>();
     
                String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(sd.getTimestamp());
                column.add( formattedDate);
                column.add("color: #FF0000");

                graphData.put((int)co2Object.getValue(), column);


            }else{
                ArrayList<String> column = new ArrayList<String>();
                String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(sd.getTimestamp());
                column.add( formattedDate);
                column.add("color: #FF0000");

                graphData.put((int)co2Object.getValue(), column);

            }

        }
        
        

      
        System.out.println(graphData + "  EEEEEEEEEEEEEE");
        model.addAttribute("teste",graphData);

    }
    return "air_quality";

}

    


        
        
        
  
}
