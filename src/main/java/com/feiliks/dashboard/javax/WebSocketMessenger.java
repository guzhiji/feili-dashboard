package com.feiliks.dashboard.javax;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;

import javax.jms.*;
import java.io.IOException;

public class WebSocketMessenger extends Thread {

    @Override
    public void run() {

        StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
        factory.setBrokerURI("tcp://localhost:61613");
        Connection connection = null;
        try {
            connection = factory.createConnection("admin", "password");
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination dest = new StompJmsDestination("/topic/dashboard");

            MessageConsumer consumer = session.createConsumer(dest);
            while (DashboardServer.running) {

                TextMessage received = (TextMessage) consumer.receive();
                if (received != null) {
                    String msg = received.getText();
                    for (javax.websocket.Session s : DashboardServer.SESSIONS) {
                        try {
                            s.getBasicRemote().sendText(msg);
                        } catch (IOException ex) {
                        }
                    }
                }

                /*
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                }
                */

            }

        } catch (JMSException e) {

            e.printStackTrace();
            if (DashboardServer.running) {
                try {
                    Thread.sleep(1000);
                    new WebSocketMessenger().start();
                } catch (InterruptedException ex) {
                }
            }

        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                }
            }
        }

    }

}
