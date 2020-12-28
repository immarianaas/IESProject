package ua.ies.project.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import ua.ies.project.model.BodyTemperature;
import ua.ies.project.model.Co2;
import ua.ies.project.model.PeopleCounter;
import ua.ies.project.repository.BodyTemperatureRepository;
import ua.ies.project.repository.Co2Repository;
import ua.ies.project.repository.PeopleCounterRepository;

@Controller
public class WebController {

    @Autowired
    Co2Repository co2rep;

    @Autowired
    BodyTemperatureRepository bodytemprep;

    @Autowired
    PeopleCounterRepository peoplecountrep;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // TODO alterar isto pq assim aparece de todas as casas!!
        // (neste momento tá a dar o ultimo de todos e n o ultimo de todos para o edificio X)
        Co2 co2 = co2rep.findTopByOrderByIdDesc();
        BodyTemperature bt = bodytemprep.findTopByOrderByIdDesc();
        //System.out.println(data);
        PeopleCounter pc = peoplecountrep.findTopByOrderByIdDesc();

        model.addAttribute("co2_data", co2);
        //System.out.println("\n\n\n"+co2+"\n\n\n");
	    model.addAttribute("bodytemp_data", bt);
	    model.addAttribute("peoplecounter_data", pc);
        return "dashboard";
    }

    @GetMapping("/air_quality")
    public String getAir_quality(Model model){
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
