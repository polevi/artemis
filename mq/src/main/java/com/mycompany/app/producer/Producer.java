package com.mycompany.app.producer;

import jakarta.jms.*;
import lombok.extern.slf4j.Slf4j;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mycompany.app.ArtemisProperties;
import com.mycompany.app.ServiceRunner;
import com.mycompany.app.ShutdownHook;

@Service("producer")
@Slf4j
public class Producer implements ServiceRunner {
    @Autowired
    ShutdownHook shutdownHook;

    @Autowired
    ArtemisProperties artemisProperties;

    @Override
    public void run() throws Exception {
        log.info("Producer started");

        log.info("Broker: {}", artemisProperties.getUrl());
        log.info("Address: {}", artemisProperties.getQueue());

        Connection connection = null;
        try (ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(artemisProperties.getUrl())) {
            connection = connectionFactory.createConnection(artemisProperties.getUsername(), artemisProperties.getPassword());
            connection.start();
    
            Session session = connection.createSession(Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(artemisProperties.getQueue());

            MessageProducer producer = session.createProducer(queue);
            producer.setTimeToLive(30000);

            log.info("Start producing messages to address: {}", artemisProperties.getQueue());

            int n = 0;
            while(!shutdownHook.isTerminating()) {
                TextMessage message = session.createTextMessage(String.format("Hello world %s !", n));
                producer.send(message);
                log.info("Message {} has been sent successfully.", n);    
                n++;
                Thread.sleep(1000);
            }
        }
        catch(JMSException e) {
            log.error("JMS Exception", e);
        }
        finally {
            if(connection != null)
                connection.close();            
        }
    }   

}
