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
    
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        // TODO alterar isto pq assim aparece de todas as casas!!
        // (neste momento tá a dar o ultimo de todos e n o ultimo de todos para o edificio X)
        Co2 co2 = co2rep.findTopByOrderByIdDesc();
        //System.out.println(data);
        Map<String, String> co2_data = new HashMap<String, String>();
        co2_data.put("id", "" + co2.getId());
        co2_data.put("timestamp", co2.getTimestamp().toString());
        co2_data.put("location", co2.getLocal());
        co2_data.put("sensor_id", "" + co2.getSensorId());
        co2_data.put("value", "" + ""+co2.getValue());
        model.addAttribute("co2_data", co2_data);

        BodyTemperature bt = bodytemprep.findTopByOrderByIdDesc();
        Map<String, String> bodytemp_data = new HashMap<String, String>();
        co2_data.put("id", "" + bt.getId());
        co2_data.put("timestamp", bt.getTimestamp().toString());
        co2_data.put("location", bt.getLocal());
        co2_data.put("sensor_id", "" + bt.getSensorId());
        co2_data.put("value", "" + ""+bt.getValue());
        model.addAttribute("bodytemp_data", bodytemp_data);

        PeopleCounter pc = peoplecountrep.findTopByOrderByIdDesc();
        Map<String, String> peoplecounter_data = new HashMap<String, String>();
        co2_data.put("id", "" + pc.getId());
        co2_data.put("timestamp", pc.getTimestamp().toString());
        co2_data.put("location", pc.getLocal());
        co2_data.put("sensor_id", "" + pc.getSensorId());
        co2_data.put("value", "" + ""+pc.getValue());
        model.addAttribute("peoplecounter_data", peoplecounter_data);

        return "dashboard";
    }
    
}
