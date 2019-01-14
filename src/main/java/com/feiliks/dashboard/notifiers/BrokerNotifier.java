package com.feiliks.dashboard.notifiers;

import com.feiliks.dashboard.spring.AbstractNotifier;
import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;

import javax.jms.*;


public class BrokerNotifier extends AbstractNotifier {

    @Override
    public void run() {

        StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
        factory.setBrokerURI("tcp://localhost:61613");
        Connection connection = null;
        try {
            connection = factory.createConnection("admin", "password");
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination dest = new StompJmsDestination("getNotifier().readConfig(brokerDest)");

            MessageConsumer consumer = session.createConsumer(dest);
            while (true) {

                TextMessage received = (TextMessage) consumer.receive();
                if (received != null)
                    notifyClient(received.getText());

                /*
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                }
                */

            }

        } catch (JMSException e) {

            e.printStackTrace();
            // TODO restart

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

