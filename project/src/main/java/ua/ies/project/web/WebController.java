package ua.ies.project.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
import ua.ies.project.model.User;
import ua.ies.project.repository.BodyTemperatureRepository;
import ua.ies.project.repository.BuildingRepository;
import ua.ies.project.repository.Co2Repository;
import ua.ies.project.repository.PeopleCounterRepository;
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
        
        for(Building b :u.getBuildings()){
            mapbuildingsAndRooms.put(b.getBuildingName(), b.getRooms());
        }
        model.addAttribute("mapbuildingsAndRooms", mapbuildingsAndRooms);

        
        return "dashboard";
    }

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

        
        //DELET%E
        Map<String, Integer> graphData = new TreeMap<>();
		graphData.put("2016", 147);
		graphData.put("2017", 1256);
		graphData.put("2018", 3856);
		graphData.put("2019", 19807);
		model.addAttribute("chartData", graphData);
   



        Map<Integer, ArrayList<String>> v = new HashMap<>();
        ArrayList<String> x = new ArrayList<String>();
        x.add("2100");
        x.add("color: #b87333");

        v.put(123, x );

      
        //System.out.println(graphData + "  EEEEEEEEEEEEEE");
        model.addAttribute("SSchacrtData", v);





        

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
