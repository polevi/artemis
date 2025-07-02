package com.mycompany.app.consumer;

import jakarta.jms.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.mycompany.app.ServiceRunner;
import com.mycompany.app.ShutdownHook;
import com.mycompany.app.configs.ArtemisConfig;
import com.mycompany.app.dao.RawDataRepository;
import com.mycompany.app.helpers.SwiftMTHelper;
import com.mycompany.app.messages.SwiftMTMessage;

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
    @Async("threadPoolTaskExecutor")
    @Retryable(value = RuntimeException.class, maxAttempts = Integer.MAX_VALUE, backoff = @Backoff(delay = 5000), listeners = {"retryListener"})
    public void run() throws Exception {
        log.info("Consumer started");

        String queue = "...";
        int batchSize = 100;
        
        try (JMSContext context = artemisConnectionFactory.createContext(JMSContext.SESSION_TRANSACTED)) {            
            context.start();
    
            Queue recvQueue = context.createQueue(queue);
            Queue replyQueue = context.createQueue("ack");
            JMSConsumer consumer = context.createConsumer(recvQueue);
            JMSProducer producer = context.createProducer();

            log.info("Start receiving messages from address: {}", queue);

            while(!shutdownHook.isTerminating()) {

                ArrayList<SwiftMTMessage> list = new ArrayList<SwiftMTMessage>();

                for(int i = 0; i < batchSize; i++) {

                    Message message = consumer.receive();
                    if (message == null)
                        break;
                    if (message instanceof ObjectMessage) {
                        list.add(message.getBody(SwiftMTMessage.class));
                    } else {
                        break;
                    }
                }

                if (list.size() > 0) {
                    try {
                        rawDataRepository.insertBatch(list);
                        log.info("Inserted batch of {} records", list.size());

                        Message replyMessage = context.createMessage();
                        producer.send(replyQueue, replyMessage);
                        log.info("Ack sent");
                    } catch(Exception e) {
                        log.error(e.getMessage());
                    }
                    context.commit();
                }                
            }
        }   
    }   
}
