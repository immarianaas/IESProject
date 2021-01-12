package ua.ies.project.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
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
import ua.ies.project.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class SensorDataController {

    @Autowired
    private SensorDataRepository sensorDataRepository;

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
       
        User ux = userRepository.findByUsername(username);
        Set<Building> buildings =  ux.getBuildings();

        Set<SensorData> allSensorsData = new HashSet<>();
        for(Building b : buildings){

            Set<Room> rooms = b.getRooms();
            if(rooms.size() != 0){
                for(Room r : rooms){
                    
                    Set<Sensor> sensors = r.getSensors();

                    if(sensors.size() != 0){
                        for(Sensor s : sensors){
                            System.out.println("TYEP  " + s.getType());
        
                            if(s.getType().equals("CO2")){
                                allSensorsData.addAll(s.getSensorsData());
                        }
                    }
                }
            }
        }


        Map<String, Integer> graphDataX = new TreeMap<>();

        Map<Integer, ArrayList<String>> graphDataY = new TreeMap<>();



        for (SensorData sd : allSensorsData) {
            Co2 co2Object= null;

            try{
                co2Object= co2Repository.findById(sd.getId()).get();
            }catch(Exception e){
                continue;
            }

            //System.out.println(graphDataX.size() + "  ENTROU ------------------------" + co2Object.getValue() + "  " +  sd.getWarn()+ "   " + sd.getTimestamp());


            if(sd.getWarn()){
                //ArrayList<String> column = new ArrayList<>();
                String formattedDate = new SimpleDateFormat("MM-dd-yyyy HH:mm").format(sd.getTimestamp());
                //column.add(formattedDate);
                //column.add("color: #FF0000");
                String y = formattedDate + "W";
                graphDataX.put(y, (int)co2Object.getValue());


                //graphDataY.put( (int)co2Object.getValue(), column);
            }else{
                String formattedDate = new SimpleDateFormat("MM-dd-yyyy HH:mm").format(sd.getTimestamp());
                graphDataX.put(formattedDate, (int)co2Object.getValue());



                
            }
    
            System.out.println(graphDataY + "  ANTES");//DEL
            
            System.out.println("  SIZZE MAP ------------------------" + graphDataY.size() );

            if(graphDataY.size() == 10){
                System.out.println(graphDataX + "  FINAL");//DEL
                model.addAttribute("graphData", graphDataY);//DEL
                break;
            }

        }


        } 
        return "air_quality";
        }

        

}

    


        
        
        
  

