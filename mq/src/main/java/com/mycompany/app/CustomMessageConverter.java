package com.mycompany.app;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;

public class CustomMessageConverter implements MessageConverter {
    @Override
    public Message toMessage(Object object, Session session) throws JMSException, 	MessageConversionException {
        // Add custom conversion logic if needed
        return toMessage(object, session);
    }

    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        // Add custom conversion logic if needed
        return fromMessage(message);
    }
}
