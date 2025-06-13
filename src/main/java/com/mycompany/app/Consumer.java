package com.mycompany.app;

import jakarta.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("--consume")
public class Consumer implements ServiceRunner {
    private static final Logger log = LoggerFactory.getLogger(Consumer.class);

    @Autowired
    ShutdownHook shutdownHook;

    @Override
    public void run(String... args) throws Exception {
        log.info("Consumer started");

        String broker_url = args[1];
        String address_name = args[2];

        log.info("Broker: {}", broker_url);
        log.info("Address: {}", address_name);

        ActiveMQConnectionFactory connectionFactory = null;
        Connection connection = null;
        try {
            connectionFactory = new ActiveMQConnectionFactory();
            connection = connectionFactory.createConnection();
            connection.start();
    
            Session session = connection.createSession(Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(address_name);
            MessageConsumer consumer = session.createConsumer(topic);

            while(!shutdownHook.isTerminating()) {
                Message message = consumer.receive();
                if (message instanceof TextMessage) {
                    log.info(((TextMessage)message).getText());
                }
            }
        }
        catch(JMSException e) {

        }
        finally {
            connection.close();            
        }
    }   

}
