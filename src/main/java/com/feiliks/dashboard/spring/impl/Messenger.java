package com.feiliks.dashboard.spring.impl;

import com.feiliks.dashboard.IMessenger;
import org.springframework.messaging.simp.SimpMessagingTemplate;


public class Messenger implements IMessenger {

    private final SimpMessagingTemplate messaging;

    public Messenger(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    @Override
    public void send(long monitorId, String source, String message) {
        messaging.convertAndSend(
                "/dashboard/notification/" +
                        monitorId + "/" + source,
                message);
    }

}
