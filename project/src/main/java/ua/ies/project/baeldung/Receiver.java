package ua.ies.project.baeldung;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Component
public class Receiver {
 
    
    @Bean
    public Queue co2() {
        return new Queue("co2", false);
    }

    /*
    @RabbitListener(queues= {"co2", "body_temperature", "people_counter"})
    public void listen(String in) {
        System.out.println("\n pls work msg: " + in);
    }
    */

    @RabbitListener(queues= "co2")
    public void listen_co2(String in) {
        System.out.println("\n CO2: " + in);
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
