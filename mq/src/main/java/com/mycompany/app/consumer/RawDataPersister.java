package com.mycompany.app.consumer;

import java.util.List;

import jakarta.jms.Session;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mycompany.app.dao.RawDataRepository;
import com.mycompany.app.messages.SwiftMTMessage;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RawDataPersister {

    RawDataRepository rawDataRepository;

    public RawDataPersister(RawDataRepository rawDataRepository) {
        this.rawDataRepository = rawDataRepository;
    }

    @JmsListener(destination = "#{@artemisConfig.queue}", containerFactory = "batchJmsListenerContainerFactory")
    @Transactional
    public void processSwiftMT(BatchMessage batch, Session session) {
        List<SwiftMTMessage> messages = batch.getMessages(SwiftMTMessage.class);
        rawDataRepository.insertBatch(messages);
        log.info("Inserted {} records", messages.size());
    }
}
