package com.mycompany.app.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Random;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.app.configs.ArtemisConfig;
import com.mycompany.app.helpers.SwiftMTHelper;
import com.mycompany.app.messages.SwiftMTMessage;

import jakarta.jms.JMSContext;
import jakarta.jms.JMSProducer;
import jakarta.jms.Queue;
import lombok.extern.slf4j.Slf4j;

@Service
@Primary
@Slf4j
public class JMSContextProducer implements IProducer {

    @Autowired 
    ActiveMQConnectionFactory artemisConnectionFactory;  

    @Autowired
    ArtemisConfig config;

    @Autowired
    ObjectMapper mapper;

    @Override
    public void run() {
        log.info("Start producing messages to address: {}", config.getQueue());

        try (JMSContext context = artemisConnectionFactory.createContext(JMSContext.SESSION_TRANSACTED)) {            
            context.start();

            Queue queue = context.createQueue(config.getQueue());
            Queue replyQueue = context.createQueue(config.getQueue() + ".reply");

            JMSProducer producer = context.createProducer();
            producer.setJMSReplyTo(replyQueue);

            Random r = new Random();
            long start = System.currentTimeMillis();
            long cnt = 0;
            while(!Thread.interrupted()) {
                int n = r.nextInt(config.getBatchSize() - 1) + 1;
                for (int i = 0; i < n; i++) {
                    long message_id = start + cnt;
                    SwiftMTMessage msg = new SwiftMTMessage(message_id, LocalDate.now(), SwiftMTHelper.createMT103(message_id));    
                    producer.send(queue, serialize(msg));
                    cnt++;
                }
                log.info("Batch of {} messages hava been sent successfully. Average rate is {} rps", n, cnt * 1000 / (System.currentTimeMillis() - start));  
                context.commit();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    byte[] serialize(Object obj) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            mapper.writeValue(out, obj);
            return out.toByteArray();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

    }
}
