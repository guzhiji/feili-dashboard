package com.feiliks.dashboard.javax;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;

import javax.jms.*;

public abstract class DashboardMonitorTask implements Runnable {

    private Connection connection;
    private Session session;
    private MessageProducer producer;

    private void connect() throws JMSException {
        if (connection == null) {
            StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
            factory.setBrokerURI("tcp://localhost:61613");

            connection = factory.createConnection("admin", "password");
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination dest = new StompJmsDestination("/topic/dashboard");
            producer = session.createProducer(dest);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        }
    }

    private void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
            }
            connection = null;
            session = null;
            producer = null;
        }
    }

    protected void finalize() {
        close();
    }

    public void broadcast(String type, String msg) {
        try {
            connect();
            TextMessage m = session.createTextMessage(type + ":" + System.currentTimeMillis() + ":" + msg);
            // m.setLongProperty("t", System.currentTimeMillis());
            producer.send(m);
        } catch (JMSException e) {
            close();
        }
    }

}
