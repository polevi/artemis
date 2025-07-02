package com.mycompany.app.services;

import java.time.LocalDate;
import java.util.Random;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

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
    ArtemisConfig congig;

    @Override
    public void run() {
        log.info("Start producing messages to address: {}", congig.getQueue());

        try (JMSContext context = artemisConnectionFactory.createContext(JMSContext.SESSION_TRANSACTED)) {            
            context.start();

            Queue queue = context.createQueue(congig.getQueue());
            JMSProducer producer = context.createProducer();

            Random r = new Random();
            int cnt = 0;
            long start = System.currentTimeMillis();
            while(!Thread.interrupted()) {
                int n = r.nextInt(congig.getBatchSize() - 1) + 1;
                for (int i = 0; i < n; i++) {
                    SwiftMTMessage msg = new SwiftMTMessage(n, LocalDate.now(), SwiftMTHelper.createMT103(n));
                    producer.send(queue, msg);
                    cnt++;
                }
                log.info("Batch of {} messages has been sent successfully. Average rate is {} rps", n, cnt * 1000 / (System.currentTimeMillis() - start));  
                context.commit();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
