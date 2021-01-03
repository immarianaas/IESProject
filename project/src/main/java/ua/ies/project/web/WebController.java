package ua.ies.project.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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
import ua.ies.project.repository.BuildingRepository;
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
    private BuildingRepository buildingRepository;
    
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
        model.addAttribute("listBuildings", u.getBuildings());
        
        //buildings and rooms of user
        // User u1 = userRepository.findByUsername(username);
        System.out.println(u.getUsername());
        Map<String, Set<Room>> mapbuildingsAndRooms = new HashMap<String, Set<Room>>();
        Map<Room, Integer[]> mapNrSensorsPerRoom = new HashMap<Room, Integer[]>();

        Map<Room, Set<Sensor>> mapRoomSensor = new HashMap<Room, Set<Sensor>>();
        Map<Sensor, Object[]> mapSensorLastValueRec = new HashMap<Sensor, Object[]>();

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
                }

                mapNrSensorsPerRoom.put(r, no_sensors);

            }
        }
        model.addAttribute("mapbuildingsAndRooms", mapbuildingsAndRooms);
        model.addAttribute("mapNrSensors", mapNrSensorsPerRoom);
        model.addAttribute("mapRoomSensorsLastVal", mapSensorLastValueRec);
        model.addAttribute("mapRoomSensor", mapRoomSensor);

        
        return "dashboard";
    }

    private Object[] getLastValueRecieved(Sensor s) {
        SensorData sd = sensdatarep.findBySensorIdOrderByTimestampDesc(s.getId()).get(0);

        if (sd instanceof Co2) {
            return new Object[] {((Co2)sd).getValue(), sd.getTimestamp(), sd.getWarn()};
        } else if (sd instanceof PeopleCounter) {
            return new Object[] {((PeopleCounter) sd).getValue(), sd.getTimestamp(), sd.getWarn()};
        } else if (sd instanceof BodyTemperature) {
            return new Object[] {((BodyTemperature) sd).getValue() , sd.getTimestamp(), sd.getWarn()};
        } else {
            System.out.println("\n\n\n\nPLEASE DONT ENTER HERE(so se for vazio) \n\n\n\n");
        }
        return null;

    }

    @Autowired
    private SensorDataRepository sensdatarep;


    @GetMapping("/air_quality")
    public String getAir_quality(Model model,  @CurrentSecurityContext(expression="authentication.name") String username){

        //buildings and rooms of user
        User u = userRepository.findByUsername(username);
        System.out.println(u.getUsername());
        Map<String, Set<Room>> mapbuildingsAndRooms = new HashMap<String, Set<Room>>();
        

        for(Building b :u.getBuildings()){
            mapbuildingsAndRooms.put(b.getBuildingName(), b.getRooms());

        }
        model.addAttribute("mapbuildingsAndRooms", mapbuildingsAndRooms);

        //...
        

        return "air_quality";
    }


    @GetMapping("/body_temp_control")
    public String getBody_Temp_Control(Model model){
        return "body_temp_control";
    }

    @GetMapping("/people_counter")
    public String getPeople_counter(Model model){
        return "people_counter";
    }
}
