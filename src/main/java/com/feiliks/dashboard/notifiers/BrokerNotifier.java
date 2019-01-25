package com.feiliks.dashboard.notifiers;

import com.feiliks.dashboard.INotifierData;
import com.feiliks.dashboard.spring.impl.AbstractNotifier;
import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;

import javax.jms.*;


public class BrokerNotifier extends AbstractNotifier {

    @Override
    public void run() {

        INotifierData notifier = getNotifier();
        String brokerUri = (String) notifier.readConfig("brokerUri");
        String brokerUser = (String) notifier.readConfig("brokerUser");
        String brokerPass = (String) notifier.readConfig("brokerPass");
        String brokerDest = (String) notifier.readConfig("brokerDest");

        StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
        factory.setBrokerURI(brokerUri);
        try (Connection connection = factory.createConnection(brokerUser, brokerPass)) {
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination dest = new StompJmsDestination(brokerDest);

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

        }

    }

}

