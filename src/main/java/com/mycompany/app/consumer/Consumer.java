package com.mycompany.app.consumer;

import jakarta.jms.*;
import lombok.extern.slf4j.Slf4j;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

 import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mycompany.app.ArtemisProperties;
import com.mycompany.app.ServiceRunner;
import com.mycompany.app.ShutdownHook;

@Service("consumer")
@Slf4j
public class Consumer implements ServiceRunner {
    @Autowired
    ShutdownHook shutdownHook;

    @Autowired
    ArtemisProperties artemisProperties;

    @Override
    public void run() throws Exception {
        log.info("Consumer started");

        log.info("Broker: {}", artemisProperties.getUrl());
        log.info("Address: {}", artemisProperties.getQueue());

        Connection connection = null;
        try (ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory()) {
            connection = connectionFactory.createConnection(artemisProperties.getUsername(), artemisProperties.getPassword());
            connection.start();
    
            Session session = connection.createSession(Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(artemisProperties.getQueue());
            MessageConsumer consumer = session.createConsumer(queue);

            log.info("Start receiving messages from address: {}", artemisProperties.getQueue());

            while(!shutdownHook.isTerminating()) {
                Message message = consumer.receive();
                if (message instanceof TextMessage) {
                    log.info(((TextMessage)message).getText());
                }
            }
        }
        catch(JMSException e) {
            log.error("JMS exception", e);
        }
        finally {
            if(connection != null)
                connection.close();            
        }
    }   
}
