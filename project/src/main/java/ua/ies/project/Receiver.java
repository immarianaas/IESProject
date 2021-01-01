package ua.ies.project;

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
import ua.ies.project.repository.Co2Repository;
import ua.ies.project.repository.PeopleCounterRepository;
import ua.ies.project.repository.BodyTemperatureRepository;

@Component
public class Receiver {

    @Autowired
    private Co2Repository co2Repository;

    @Autowired
    private BodyTemperatureRepository bodyTemperatureRepository;

    @Autowired
    private PeopleCounterRepository peopleCounterRepository;


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
        //System.out.println("\n CO2: " + in);

        try {
            input = mapper.readValue(in, Co2.class);
            //input.setWarn(warn);
            co2Repository.save(input);
            System.out.println("Co2 object saved to database!!");
            System.out.println(input);
        } catch (Exception e) {
            System.err.println("Error parsing JSON to Co2 object.");
            //e.printStackTrace();
        } 
        //System.out.println(input);
    }

    @RabbitListener(queues="body_temperature")
    public void listen_temp(String in) {
        BodyTemperature input = null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES , true);
        //System.out.println("\n BODY TEMP: " + in);

        try {
            input = mapper.readValue(in, BodyTemperature.class);
            bodyTemperatureRepository.save(input);
            System.out.println("BodyTemperature object saved to database!!");
        } catch (Exception e) {
            System.err.println("Error parsing JSON to BodyTemperature object.");
            //e.printStackTrace();
        } 
        //System.out.println(input);

    }

    @RabbitListener(queues="people_counter")
    public void listen_counter(String in) {
        //System.out.println("\n PEOPLE COUNTER: " + in);
        PeopleCounter input = null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES , true);
        //System.out.println("\n CO2: " + in);

        try {
            input = mapper.readValue(in, PeopleCounter.class);
            peopleCounterRepository.save(input);
            System.out.println("PeopleCounter object saved to database!!");
        } catch (Exception e) {
            System.err.println("Error parsing JSON to PeopleCounter object.");
            //e.printStackTrace();
        } 
        //System.out.println(input);
    
    }




}
