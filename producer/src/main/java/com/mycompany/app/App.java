package com.mycompany.app;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.core.JmsTemplate;

import com.mycompany.app.configs.ArtemisConfig;
import com.mycompany.app.helpers.SwiftMTHelper;
import com.mycompany.app.messages.SwiftMTMessage;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class App implements CommandLineRunner {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    ArtemisConfig congig;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        log.info("Start producing messages to address: {}", congig.getQueue());

        jmsTemplate.setDeliveryDelay(5000);
        
        int n = 0;
        while(!Thread.interrupted()) {
            SwiftMTMessage msg = new SwiftMTMessage(n, LocalDate.now(), SwiftMTHelper.createMT103(n));
            jmsTemplate.convertAndSend(congig.getQueue(), msg);
            
            if (n % congig.getBatchSize() == 0) {
                log.info("Batch of {} messages has been sent successfully.", congig.getBatchSize());  
            }

            n++;
        }
    }
}
