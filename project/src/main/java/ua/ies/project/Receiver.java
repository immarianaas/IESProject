package ua.ies.project;

import javax.transaction.Transactional;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import ua.ies.project.model.BodyTemperature;
import ua.ies.project.model.Co2;
import ua.ies.project.model.PeopleCounter;
import ua.ies.project.model.Room;
import ua.ies.project.model.Sensor;
import ua.ies.project.repository.Co2Repository;
import ua.ies.project.repository.PeopleCounterRepository;
import ua.ies.project.repository.SensorDataRepository;
import ua.ies.project.repository.SensorRepository;
import ua.ies.project.repository.BodyTemperatureRepository;

@Transactional
@Component
public class Receiver {

    @Autowired
    private Co2Repository co2Repository;

    @Autowired
    private SensorDataRepository sensordatarep;

    @Autowired
    private BodyTemperatureRepository bodyTemperatureRepository;

    @Autowired
    private PeopleCounterRepository peopleCounterRepository;

    @Autowired
    private SensorRepository sensorrep;
    


    @Bean
    public Queue co2() {
        return new Queue("co2", false);
    }

    @Bean
    public Queue body_temperature() {
        return new Queue("body_temperature", false);
    }

    @Bean
    public Queue people_counter() {
        return new Queue("people_counter", false);
    }

    /*
     * @RabbitListener(queues= {"co2", "body_temperature", "people_counter"}) public
     * void listen(String in) { System.out.println("\n pls work msg: " + in); }
     */

    @RabbitListener(queues = "co2")
    public void listen_co2(String in) {
        Co2 input = null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES , true);
        Object[] s = getSensorId(in);


        try {
            input = mapper.readValue((String) s[0], Co2.class);

            //input.setWarn(warn);
            Sensor sens = (Sensor) s[1];
            sens.addSensorsData(input);
            sens = sensorrep.save(sens);

            input.setSensor(sens);
            if (isWarningCo2(input)) input.setWarn(true);
            sensordatarep.save(input);
            co2Repository.save(input);
            System.out.println("\t>>\tCo2 object saved to database!!");
        } catch (Exception e) {
            //e.printStackTrace();
            System.err.println("\t!!\tError parsing JSON to Co2 object.");
        } 
        //System.out.println(input);
    }

    private boolean isWarningCo2(Co2 input) {
            Room r = input.getSensor().getRoom();
            return r.getMaxLevelCo2() < input.getValue();
        }


    private Object[] getSensorId(String s) {
        String s_to_return = s.substring(0, s.indexOf(", 'sensorId'")) + "}";
        String number = s.substring(s.lastIndexOf(": ")+2, s.length()-1);
        long id = -1;
        try {
            id = Long.parseLong(number);
            System.out.println("\t>>\tnumber parsed gud!");
        } catch(Exception e) {
            System.err.println("\t!!\tError parsing ID.");
            return null;
        }

        Sensor sens = sensorrep.findOneBySensorId(id);
        if (sens == null) {
            System.err.println("\t!!\tThere is no sensor with that ID.");
            return null;
        }
        System.out.println("\t>>\tsensor found gud!");

        return new Object[] {s_to_return, sens};
    }

    @RabbitListener(queues="body_temperature")
    public void listen_temp(String in) {
        BodyTemperature input = null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES , true);

        Object[] o = getSensorId(in);

        try {
            input = mapper.readValue((String) o[0], BodyTemperature.class);

            Sensor sens = (Sensor) o[1];
            sens.addSensorsData(input);
            sens = sensorrep.save(sens);
            
            input.setSensor(sens);
            if (isWarningBodyTemp(input)) input.setWarn(true);

            sensordatarep.save(input);
            bodyTemperatureRepository.save(input);
            System.out.println("\t>>\tBodyTemperature object saved to database!!");
        } catch (Exception e) {
            System.err.println("\t!!\tError parsing JSON to BodyTemperature object.");
        } 
    }

    private boolean isWarningBodyTemp(BodyTemperature input) {
        Room r = input.getSensor().getRoom();
        return r.getMaxTemperature() < input.getValue();
    }

    private boolean isWarningPeopleCounter(PeopleCounter input) {
        Room r = input.getSensor().getRoom();
        return r.getMaxOccupation() < input.getValue();
    }

    @RabbitListener(queues="people_counter")
    public void listen_counter(String in) {
        //System.out.println("\n PEOPLE COUNTER: " + in);
        PeopleCounter input = null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES , true);
        //System.out.println("\n CO2: " + in);
        Object[] o = getSensorId(in);

        try {
            input = mapper.readValue((String) o[0], PeopleCounter.class);

            Sensor sens = (Sensor) o[1];
            sens.addSensorsData(input);
            sens = sensorrep.save(sens);
            
            input.setSensor(sens);
            if (isWarningPeopleCounter(input)) input.setWarn(true);

            sensordatarep.save(input);

            peopleCounterRepository.save(input);
            System.out.println("\t>>\tPeopleCounter object saved to database!!");
        } catch (Exception e) {
            System.err.println("\t!!\tError parsing JSON to PeopleCounter object.");
        } 
    
    }




}
