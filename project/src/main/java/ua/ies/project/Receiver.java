package ua.ies.project;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import ua.ies.project.model.Co2;
import ua.ies.project.repository.Co2Repository;

@Component
public class Receiver {

    @Autowired
    private Co2Repository co2Repository;

    @Bean
    public Queue co2() {
        return new Queue("co2", false);
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

        try {
            input = mapper.readValue(in, Co2.class);

        } catch (Exception e) {
            e.printStackTrace();
        } 
        System.out.println("\n CO2: " + in);
        System.out.println(input);
        co2Repository.save(input);
        System.out.println("Object saved to database!!");
    }

    @RabbitListener(queues="body_temperature")
    public void listen_temp(String in) {
        System.out.println("\n BODY TEMP: " + in);
    }

    @RabbitListener(queues="people_counter")
    public void listen_counter(String in) {
        System.out.println("\n PEOPLE COUNTER: " + in);
    }




}
