package ua.ies.project.web;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import ua.ies.project.model.BodyTemperature;
import ua.ies.project.model.Co2;
import ua.ies.project.model.PeopleCounter;
import ua.ies.project.model.Room;
import ua.ies.project.model.Sensor;
import ua.ies.project.model.SensorData;
import ua.ies.project.model.User;
import ua.ies.project.repository.BodyTemperatureRepository;
import ua.ies.project.repository.Co2Repository;
import ua.ies.project.repository.PeopleCounterRepository;
import ua.ies.project.repository.SensorDataRepository;
import ua.ies.project.repository.UserRepository;
import ua.ies.project.model.Building;

@Controller
public class WebController {

    @Autowired
    Co2Repository co2rep;

    @Autowired
    BodyTemperatureRepository bodytemprep;

    @Autowired
    PeopleCounterRepository peoplecountrep;

    @Autowired
    private UserRepository userRepository;
  


    @GetMapping("/")
    public String home(Model model) {
        return "redirect:/dashboard";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, @CurrentSecurityContext(expression="authentication.name") String username) {
        /* // isto tem mm de ser alterado
        Co2 co2 = co2rep.findTopByOrderByIdDesc();
        BodyTemperature bt = bodytemprep.findTopByOrderByIdDesc();
        //System.out.println(data);
        PeopleCounter pc = peoplecountrep.findTopByOrderByIdDesc();

        model.addAttribute("co2_data", co2);
	    model.addAttribute("bodytemp_data", bt);
        model.addAttribute("peoplecounter_data", pc);
        */
        //load buildings

        User u = userRepository.findByUsername(username);
        if (u == null) return "redirect:/login";
        model.addAttribute("listBuildings", u.getBuildings());
        
        /*
        Page<SensorData> p = sensdatarep.findAll(
            PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "timestamp"))
        );
        */

        //buildings and rooms of user
        // User u1 = userRepository.findByUsername(username);
        System.out.println(u.getUsername());
        Map<String, Set<Room>> mapbuildingsAndRooms = new HashMap<String, Set<Room>>();
        Map<Room, Integer[]> mapNrSensorsPerRoom = new HashMap<Room, Integer[]>();

        Map<Room, Set<Sensor>> mapRoomSensor = new HashMap<Room, Set<Sensor>>();
        Map<Sensor, Object[]> mapSensorLastValueRec = new HashMap<Sensor, Object[]>();

        Set<Sensor> sensorsWarning = new HashSet<Sensor>();

        for(Building b :u.getBuildings()){
            mapbuildingsAndRooms.put(b.getBuildingName(), b.getRooms());
            for (Room r : b.getRooms()) {
                Integer[] no_sensors = new Integer[3];
                no_sensors[0] = 0;
                no_sensors[1] = 0;
                no_sensors[2] = 0;
                
                mapRoomSensor.put(r, r.getSensors());

                for (Sensor s : r.getSensors()) {
                    if (s.getType().equals("CO2")) {
                        no_sensors[0] += 1;
                    } else if (s.getType().equals("BODY_TEMPERATURE")) {
                        no_sensors[1] += 1;
                    } else if (s.getType().equals("PEOPLE_COUNTER")) {
                        no_sensors[2] += 1;
                    }
                    Object[] data =  getLastValueRecieved(s);
                    mapSensorLastValueRec.put(s, data);
                    if (data != null && (boolean) data[2])
                        sensorsWarning.add(s);
                }

                mapNrSensorsPerRoom.put(r, no_sensors);

            }
        }
        model.addAttribute("mapbuildingsAndRooms", mapbuildingsAndRooms);
        model.addAttribute("mapNrSensors", mapNrSensorsPerRoom);
        model.addAttribute("mapRoomSensorsLastVal", mapSensorLastValueRec);
        model.addAttribute("mapRoomSensor", mapRoomSensor);

        model.addAttribute("mapSensorWarning", sensorsWarning);

        
        return "dashboard";
    }

    private Object[] getLastValueRecieved(Sensor s) {
        SensorData sd;



        try {
            // sd = sensdatarep.findBySensorIdOrderByTimestampDesc(s.getId()).get(0);
            Page<SensorData> p = sensdatarep.findBySensorId( s.getId(),
            PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "timestamp"))
            );
            sd = p.get().findFirst().get();
    
        } catch (Exception e) { return null; }

        if (sd instanceof Co2) {
            return new Object[] {((Co2)sd).getValue(), sd.getTimestamp(), sd.getWarn()};
        } else if (sd instanceof PeopleCounter) {
            return new Object[] {((PeopleCounter) sd).getValue(), sd.getTimestamp(), sd.getWarn()};
        } else if (sd instanceof BodyTemperature) {
            return new Object[] {((BodyTemperature) sd).getValue() , sd.getTimestamp(), sd.getWarn()};
        }
        return null;
    }

    @Autowired
    private SensorDataRepository sensdatarep;


    @GetMapping("/air_quality")
    public String getAir_quality(Model model,  @CurrentSecurityContext(expression="authentication.name") String username){
        

        //buildings and rooms of user
        User u = userRepository.findByUsername(username);

        //Building List
        //vai buscar ao 
        model.addAttribute("listBuildingsAQ", u.getBuildings());


    return "air_quality";
        
    }

    

    @GetMapping("/body_temp_control")
    public String getBody_Temp_Control(Model model, @CurrentSecurityContext(expression="authentication.name") String username){

        //buildings and rooms of user
        User u = userRepository.findByUsername(username);

        //Building List
        model.addAttribute("listBuildingsBT", u.getBuildings());

    
    return "body_temp_control";


        
    }











    @GetMapping("/people_counter")
    public String getPeople_counter(Model model,  @CurrentSecurityContext(expression="authentication.name") String username){

        //buildings and rooms of user
        User u = userRepository.findByUsername(username);

        //Building List
        //vai buscar ao 
        model.addAttribute("listBuildingsPC", u.getBuildings());
   
        /*
        //Graficos
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
        
                            if(s.getType().equals("PEOPLE_COUNTER")){
                                allSensorsData.addAll(s.getSensorsData());
                        }
                    }
                }
            }
        }


        Map<String, Integer> graphDataX = new TreeMap<>();

        Map<Integer, ArrayList<String>> graphDataY = new TreeMap<>();



        for (SensorData sd : allSensorsData) {
            PeopleCounter pcObject= null;

            try{
                pcObject= peoplecountrep.findById(sd.getId()).get();
            }catch(Exception e){
                continue;
            }

            if(sd.getWarn()){
                String formattedDate = new SimpleDateFormat("MM-dd-yyyy HH:mm").format(sd.getTimestamp());
      
                String y = formattedDate + "W";
                graphDataX.put(y, (int)pcObject.getValue());
            }else{
                String formattedDate = new SimpleDateFormat("MM-dd-yyyy HH:mm").format(sd.getTimestamp());
                graphDataX.put(formattedDate, (int)pcObject.getValue());  
            }
    
            
            System.out.println("  SIZZE MAP ------------------------" + graphDataX.size() );

            if(graphDataX.size() == 10){
                model.addAttribute("graphDataPC", graphDataX);//DEL

                Map<Integer, Integer> resultMap = new TreeMap<>();

                for (String key : graphDataX.keySet()) {
                    Integer value = graphDataX.get(key);
                    
                    if (resultMap.containsKey(value)) {
                        resultMap.put(value, resultMap.get(value) + 1);
                    } else {
                        resultMap.put(value, 1);
                    }
                }
                model.addAttribute("graphDataPC_pie", resultMap);
                break;

                
            }

        } */
    
        return "people_counter";
    }
}
