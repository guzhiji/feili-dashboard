package com.feiliks.dashboard.spring.impl;

import com.feiliks.dashboard.IMessenger;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class Messenger implements IMessenger {

    private final long notifierId;
    private final SimpMessagingTemplate messaging;

    public Messenger(long notifierId, SimpMessagingTemplate messaging) {
        this.notifierId = notifierId;
        this.messaging = messaging;
    }

    @Override
    public void send(String text) {
        messaging.convertAndSend(
                "/dashboard/notifier/" + notifierId,
                text);
    }

}
