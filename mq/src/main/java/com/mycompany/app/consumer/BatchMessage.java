package com.mycompany.app.consumer;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;

public class BatchMessage implements Message {

    private int batchSize;
    private ArrayList<Object> messages = new ArrayList<Object>();

    public BatchMessage(int batchSize) {
        this.batchSize = batchSize;
    }

    public <M> List<M> getMessages(Class<M> clazz) {
        return this.messages.stream().map(a -> clazz.cast(a)).collect(Collectors.toList());
    }

    public boolean isEmpty() {
        return messages.isEmpty();
    }

    public boolean releaseAfterMessage(Object message) {
        if (message != null) {
            messages.add(message);
        }
        // Are we ready to release?
        return message == null || messages.size() >= batchSize;
    }

    @Override
    public String getJMSMessageID() throws JMSException {
        return null;
    }

    @Override
    public void setJMSMessageID(String id) throws JMSException {
    }

    @Override
    public long getJMSTimestamp() throws JMSException {
        return 0;
    }

    @Override
    public void setJMSTimestamp(long timestamp) throws JMSException {
    }

    @Override
    public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
        return null;
    }

    @Override
    public void setJMSCorrelationIDAsBytes(byte[] correlationID) throws JMSException {
    }

    @Override
    public void setJMSCorrelationID(String correlationID) throws JMSException {
    }

    @Override
    public String getJMSCorrelationID() throws JMSException {
        return null;
    }

    @Override
    public Destination getJMSReplyTo() throws JMSException {
        return null;
    }

    @Override
    public void setJMSReplyTo(Destination replyTo) throws JMSException {
    }

    @Override
    public Destination getJMSDestination() throws JMSException {
        return null;
    }

    @Override
    public void setJMSDestination(Destination destination) throws JMSException {
    }

    @Override
    public int getJMSDeliveryMode() throws JMSException {
        return 0;
    }

    @Override
    public void setJMSDeliveryMode(int deliveryMode) throws JMSException {
    }

    @Override
    public boolean getJMSRedelivered() throws JMSException {
        return false;
    }

    @Override
    public void setJMSRedelivered(boolean redelivered) throws JMSException {
    }

    @Override
    public String getJMSType() throws JMSException {
        return null;
    }

    @Override
    public void setJMSType(String type) throws JMSException {
    }

    @Override
    public long getJMSExpiration() throws JMSException {
        return 0;
    }

    @Override
    public void setJMSExpiration(long expiration) throws JMSException {
    }

    @Override
    public long getJMSDeliveryTime() throws JMSException {
        return 0;
    }

    @Override
    public void setJMSDeliveryTime(long deliveryTime) throws JMSException {
    }

    @Override
    public int getJMSPriority() throws JMSException {
        return 0;
    }

    @Override
    public void setJMSPriority(int priority) throws JMSException {
    }

    @Override
    public void clearProperties() throws JMSException {
    }

    @Override
    public boolean propertyExists(String name) throws JMSException {
        return false;
    }

    @Override
    public boolean getBooleanProperty(String name) throws JMSException {
        return false;
    }

    @Override
    public byte getByteProperty(String name) throws JMSException {
        return 0;
    }

    @Override
    public short getShortProperty(String name) throws JMSException {
        return 0;
    }

    @Override
    public int getIntProperty(String name) throws JMSException {
        return 0;
    }

    @Override
    public long getLongProperty(String name) throws JMSException {
        return 0;
    }

    @Override
    public float getFloatProperty(String name) throws JMSException {
        return 0;
    }

    @Override
    public double getDoubleProperty(String name) throws JMSException {
        return 0;
    }

    @Override
    public String getStringProperty(String name) throws JMSException {
        return null;
    }

    @Override
    public Object getObjectProperty(String name) throws JMSException {
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Enumeration getPropertyNames() throws JMSException {
        return null;
    }

    @Override
    public void setBooleanProperty(String name, boolean value) throws JMSException {
    }

    @Override
    public void setByteProperty(String name, byte value) throws JMSException {
    }

    @Override
    public void setShortProperty(String name, short value) throws JMSException {
    }

    @Override
    public void setIntProperty(String name, int value) throws JMSException {
    }

    @Override
    public void setLongProperty(String name, long value) throws JMSException {
    }

    @Override
    public void setFloatProperty(String name, float value) throws JMSException {
    }

    @Override
    public void setDoubleProperty(String name, double value) throws JMSException {
    }

    @Override
    public void setStringProperty(String name, String value) throws JMSException {
    }

    @Override
    public void setObjectProperty(String name, Object value) throws JMSException {
    }

    @Override
    public void acknowledge() throws JMSException {
    }

    @Override
    public void clearBody() throws JMSException {
    }

    @Override
    public <T> T getBody(Class<T> c) throws JMSException {
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean isBodyAssignableTo(Class c) throws JMSException {
        return false;
    }
        
}
