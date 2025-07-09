package com.mycompany.app.converters;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import com.mycompany.app.messages.SwiftMTMessage;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;

@Component
public class SwiftMTMessageConverter implements MessageConverter{

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
            SwiftMTMessage m = message.getBody(SwiftMTMessage.class);
            m.setMessage(message);
            return m;
        } else {
            return null;
        }
    }
}
