package com.mycompany.app.consumer;

import java.util.List;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

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
    public void processSwiftMT(BatchMessage batch) {
        List<SwiftMTMessage> messages = batch.getMessages(SwiftMTMessage.class);
        rawDataRepository.insertBatch(messages);
        log.info("Inserted {} records", messages.size());
    }
}
