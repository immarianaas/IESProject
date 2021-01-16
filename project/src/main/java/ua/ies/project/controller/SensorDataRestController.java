package ua.ies.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ua.ies.project.model.Role;
import ua.ies.project.model.Sensor;
import ua.ies.project.model.SensorData;
import ua.ies.project.model.User;
import ua.ies.project.repository.BodyTemperatureRepository;
import ua.ies.project.repository.Co2Repository;
import ua.ies.project.repository.PeopleCounterRepository;
import ua.ies.project.repository.SensorDataRepository;
import ua.ies.project.repository.SensorRepository;
import ua.ies.project.repository.UserRepository;

import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class SensorDataRestController {

    @Autowired
    SensorDataRepository sensdatarep;

    @Autowired
    UserRepository userrep;

    @Autowired
    SensorRepository sensrep;

    public boolean checkIfAdmin(String uname) {
        User u = userrep.findByUsername(uname);
        if (u.getRoles() != null)
            for (Role r : u.getRoles()) 
                if (r.getName().equals("admin")) return true;
        return false;
    }



    public boolean checkIfMine(String uname, long data_id) {
        //User u = userrep.findByUsername(uname);
        SensorData sd = sensdatarep.findById(data_id).get();
        for (User u : sd.getSensor().getRoom().getBuilding().getUsers()) {
            if (u.getUsername().equals(uname)) return true;
        }
        return false;
    }
    

    public static EntityModel<Map<String, Object>> getSensorDataEntityModel(String username, SensorData sd) {
        Map<String, Object> sd_map = sd.convertToMap();
        Long id = sd.getId();
        return EntityModel.of(sd_map,
            linkTo(methodOn(SensorRestController.class).sensorById(username, sd.getSensor().getId())).withRel("sensor"),
            linkTo(methodOn(SensorDataRestController.class).sensordataById(username, id)).withSelfRel() // <- change
            );
    }

    @Autowired
    Co2Repository co2rep;

    @Autowired
    BodyTemperatureRepository btemprep;

    @Autowired
    PeopleCounterRepository pcounterrep;

    @GetMapping("/api/sensordata/{id}") // -> admin ou meu
    public EntityModel<Map<String, Object>> sensordataById(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable Long id) {
        if (!(checkIfAdmin(username)) && !(checkIfMine(username, id))) throw new AccessDeniedException("403 returned");
        SensorData sd;
        try {
            sd = sensdatarep.getOne(id);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);    
        }
        return getSensorDataEntityModel(username, sd);
    }

    /*
    @GetMapping("/api/sensordata/all") // -> admin
    public List<EntityModel<Map<String, Object>>> allSensordata(@CurrentSecurityContext(expression="authentication.name") String username) {
        if (!checkIfAdmin(username)) throw new AccessDeniedException("403 returned");
        
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();

        for (SensorData sd : sensdatarep.findAll()) {
            l.add(getSensorDataEntityModel(username, sd));
        }
        return l;
    }
    */

    @GetMapping("/api/sensordata/all") // -> admin
    public List<EntityModel<Map<String, Object>>> allSensordata(@CurrentSecurityContext(expression="authentication.name") String username,
    @RequestParam(required = false) Integer pageNo,
    @RequestParam(required = false) Integer pageSize
    ) {
        if (!checkIfAdmin(username)) throw new AccessDeniedException("403 returned");
        
        List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();

        if (pageNo == null) pageNo = 0;
        if (pageSize == null) pageSize = 50;

        Page<SensorData> p = sensdatarep.findAll(
            PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "timestamp"))
        );


        for (SensorData sd : p) {
            l.add(getSensorDataEntityModel(username, sd));
        }
        return l;
    }
// ---- sem restricoes (e nao testado tambem) ----

    public boolean checkIfSensorIsMine(String uname, long sens_id) {
        //User u = userrep.findByUsername(uname);
        Sensor s = sensrep.findById(sens_id).get();
        for (User u : s.getRoom().getBuilding().getUsers()) {
            if (u.getUsername().equals(uname)) return true;
        }
        return false;
    }

    @GetMapping("/api/sensor/{id}/data/latest") 
    public EntityModel<Map<String, Object>> getLatestFromId(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable long id) {
        // ver. do user em baixo!
        Sensor s;
        try {
            s = sensrep.findById(id).get();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return getLatestFromSensor(username, s);
    }

    @GetMapping("/api/sensor/sensorid/{id}/data/latest") 
    public EntityModel<Map<String, Object>> getLatestFromSensorId(@CurrentSecurityContext(expression="authentication.name") String username, @PathVariable long id) {
        // ver. do user em baixo!
        
        Sensor s = sensrep.findOneBySensorId(id);        
        if (s==null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return getLatestFromSensor(username, s);
    }

    private EntityModel<Map<String, Object>> getLatestFromSensor(String username, Sensor s) {
        // -- verificacoes aqui apenas (n nas 2 funcoes em cima)
        if (!(checkIfAdmin(username)) && !(checkIfSensorIsMine(username, s.getId()))) throw new AccessDeniedException("403 returned");

        SensorData sd = null;
        try {
            Page<SensorData> p = sensdatarep.findBySensorId( s.getId(),
                PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "timestamp"))
            );
            sd = p.get().findFirst().get();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // SensorData sd = sensdatarep.findBySensorIdOrderByTimestampDesc(s.getSensorId()).get(0);
        // return getSensorDataEntityModel(username, sd);
        return getSensorDataEntityModel(username, sd);
    }

    @GetMapping("/api/sensor/{id}/data")    // pode dar igual ao /all
    public List<EntityModel<Map<String, Object>>> getDataInRangeOptional(
        @CurrentSecurityContext(expression="authentication.name") String username, 
        @PathVariable long id,
        @RequestParam(required = false) String begin,
        @RequestParam(required = false) String end,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) Boolean warningsOnly

        ) {
            if (!(checkIfAdmin(username)) && !(checkIfSensorIsMine(username, id))) throw new AccessDeniedException("403 returned");

            Sensor s;
            try {
                s = sensrep.findById(id).get();
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            Date b = null;
            Date e = null;

            try {
            if (begin != null)
                b = SensorData.parseDate(begin);
            if (end != null)
                e = SensorData.parseDate(end);

            } catch (Exception ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            if (type != null && !(type.equals("CO2") || type.equals("PEOPLE_COUNTER") || type.equals("BODY_TEMPERATURE")))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

            List<EntityModel<Map<String, Object>>> l = new ArrayList<EntityModel<Map<String, Object>>>();
            List<SensorData> datalist = sensdatarep.findBySensorIdOrderByTimestampAsc(s.getSensorId());
            for (SensorData sd : datalist) {
                if (b != null)
                    // se a data for menor que o 'begin', tirar
                    if (sd.getTimestamp().compareTo(b) < 0)
                        continue;
                
                if (e != null)
                    // se a data for maior q o 'end', tambem tirar
                    if (sd.getTimestamp().compareTo(e)>0)
                        continue;

                if (type != null)
                    if (!(sd.getSensor().getType().equals(type)))
                        continue;

                if (warningsOnly != null && warningsOnly == true )
                    if (sd.getWarn() != true)
                        continue;

                
                l.add(getSensorDataEntityModel(username, sd));
            }
            return l;
        }


}

