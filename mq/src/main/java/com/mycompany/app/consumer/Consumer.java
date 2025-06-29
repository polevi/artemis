package com.mycompany.app.consumer;

import jakarta.jms.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.mycompany.app.ArtemisConfig;
import com.mycompany.app.ServiceRunner;
import com.mycompany.app.ShutdownHook;
import com.mycompany.app.swiftmt.SwiftMTHelper;

@Service("consumer")
@Slf4j
public class Consumer implements ServiceRunner {
    @Autowired
    ShutdownHook shutdownHook;

    @Autowired
    ArtemisConfig artemisProperties;

    @Autowired
    SwiftMTHelper swiftMTHelper;    

    @Autowired 
    RawDataRepository rawDataRepository;

    @Autowired 
    ActiveMQConnectionFactory artemisConnectionFactory;    

    @Override
    @Async
    public void run() throws Exception {
        log.info("Consumer started");
        
        try (JMSContext context = artemisConnectionFactory.createContext(JMSContext.SESSION_TRANSACTED)) {            
            context.start();
    
            Queue queue = context.createQueue(artemisProperties.getQueue());
            JMSConsumer consumer = context.createConsumer(queue);

            log.info("Start receiving messages from address: {}", artemisProperties.getQueue());

            while(!Thread.currentThread().isInterrupted()) {

                ArrayList<Object[]> list = new ArrayList<Object[]>();

                for(int i = 0; i < artemisProperties.getBatchSize(); i++) {

                    Message message = consumer.receive();
                    if (message != null) {
                        String msg = ((TextMessage)message).getText();
                        int id = swiftMTHelper.parseMessageId(msg);
                        list.add(new Object[] { id, LocalDate.now(), msg });
                    } else {
                        break;
                    }
                }

                if (list.size() > 0) {
                    try {
                        rawDataRepository.insertBatch(list);
                        log.info("Inserted batch of {} records", list.size());
                    } catch(Exception e) {
                        log.error(e.getMessage());
                    }
                    context.commit();
                }                
            }
        }
    }   
}
