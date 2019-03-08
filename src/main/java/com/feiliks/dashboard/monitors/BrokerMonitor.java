package com.feiliks.dashboard.monitors;

import com.feiliks.dashboard.IMonitorInfo;
import com.feiliks.dashboard.AbstractMonitor;
import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;

import javax.jms.*;


public class BrokerMonitor extends AbstractMonitor {

    public final class Task extends AbstractMonitor.Task {

        @Override
        public void run() {

            IMonitorInfo notifier = getMonitorInfo();
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
                        notifyClient("", received.getText());

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

    public BrokerMonitor() {
        super(Task.class, false);
    }

}

