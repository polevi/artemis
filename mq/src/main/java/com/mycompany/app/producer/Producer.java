package com.mycompany.app.producer;

import jakarta.jms.*;
import lombok.extern.slf4j.Slf4j;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.mycompany.app.ArtemisConfig;
import com.mycompany.app.ServiceRunner;
import com.mycompany.app.ShutdownHook;
import com.mycompany.app.swiftmt.SwiftMTHelper;

@Service("producer")
@Slf4j
public class Producer implements ServiceRunner {
    @Autowired
    ShutdownHook shutdownHook;

    @Autowired
    ArtemisConfig artemisProperties;

    @Autowired
    SwiftMTHelper swiftMTHelper;

    @Autowired 
    ActiveMQConnectionFactory artemisConnectionFactory;       

    @Override
    @Async
    public void run() throws Exception {
        log.info("Producer started");

        try (Connection connection = artemisConnectionFactory.createConnection()) {
            connection.start();
    
            Session session = connection.createSession(Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(artemisProperties.getQueue());

            MessageProducer producer = session.createProducer(queue);
            producer.setTimeToLive(30000);

            log.info("Start producing messages to address: {}", artemisProperties.getQueue());

            int n = 0;
            while(!shutdownHook.isTerminating()) {
                TextMessage message = session.createTextMessage(swiftMTHelper.createMT103(n));
                producer.send(message);

                if (n % artemisProperties.getBatchSize() == 0) {
                    log.info("Batch of {} messages has been sent successfully.", artemisProperties.getBatchSize());  
                }

                n++;
            }
        }
        catch(JMSException e) {
            log.error("JMS Exception", e);
        }
    }   

}
