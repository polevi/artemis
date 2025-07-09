package com.mycompany.app.converters;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.app.messages.SwiftMTMessage;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;

@Component
public class SwiftMTMessageConverter implements MessageConverter{

    ObjectMapper mapper;

    public SwiftMTMessageConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
        SwiftMTMessage obj = (SwiftMTMessage)object;
        Message msg = session.createObjectMessage(obj);
        msg.setJMSMessageID(String.format("ID:%d", obj.getId()));
        return msg;
    }

    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        if (message != null) {
            SwiftMTMessage m = deserialize(message.getBody(byte[].class));
            m.setMessage(message);
            return m;
        } else {
            return null;
        }
    }

    SwiftMTMessage deserialize(byte[] data) {
        try(ByteArrayInputStream in = new ByteArrayInputStream(data)) {
            return mapper.readValue(in, SwiftMTMessage.class);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }    
}
