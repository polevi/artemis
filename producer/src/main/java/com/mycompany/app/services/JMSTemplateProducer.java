package com.mycompany.app.services;

import java.time.LocalDate;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.mycompany.app.configs.ArtemisConfig;
import com.mycompany.app.helpers.SwiftMTHelper;
import com.mycompany.app.messages.SwiftMTMessage;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JMSTemplateProducer implements IProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    ArtemisConfig congig;

    @Override
    public void run() {
        log.info("Start producing messages to address: {}", congig.getQueue());

        //jmsTemplate.setDeliveryDelay(5000);

        Random r = new Random();
        long cnt = 0;
        long start = System.currentTimeMillis();
        while(!Thread.interrupted()) {
            int n = r.nextInt(congig.getBatchSize() - 1) + 1;
            for (int i = 0; i < n; i++) {
                SwiftMTMessage msg = new SwiftMTMessage(cnt, LocalDate.now(), SwiftMTHelper.createMT103(n));
                jmsTemplate.convertAndSend(congig.getQueue(), msg);
                cnt++;
            }
            log.info("Batch of {} messages has been sent successfully. Average rate is {} rps", n, cnt * 1000 / (System.currentTimeMillis() - start));  
        }
    }

}
