package com.mycompany.app;

import jakarta.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("--produce")
public class Producer implements ServiceRunner {
    private static final Logger log = LoggerFactory.getLogger(Producer.class);

    @Autowired
    ShutdownHook shutdownHook;

    @Override
    public void run(String... args) throws Exception {
        log.info("Producer started");

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
            MessageProducer producer = session.createProducer(topic);

            int n = 0;
            while(!shutdownHook.isTerminating()) {
                TextMessage message = session.createTextMessage(String.format("Hello world %s !", n));
                message.setJMSExpiration(System.currentTimeMillis() + 30000);
                producer.send(message);
                log.info("Message {} has been sent successfully.", n);    
                n++;
                Thread.sleep(1000);
            }
        }
        catch(JMSException e) {

        }
        finally {
            connection.close();            
        }
    }   

}
