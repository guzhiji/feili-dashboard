package com.feiliks.dashboard.spring.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feiliks.dashboard.IMessenger;
import com.feiliks.dashboard.INotifier;
import com.feiliks.dashboard.INotifierData;
import com.feiliks.dashboard.NotifierMessage;


public abstract class AbstractNotifier implements INotifier {

    private IMessenger messenger;
    private INotifierData notifier;

    @Override
    public void initNotifier(INotifierData data, IMessenger messenger) {
        this.notifier = data;
        this.messenger = messenger;
    }

    @Override
    public INotifierData getNotifier() {
        return notifier;
    }

    @Override
    public void notifyClient(String message) {
        if (messenger != null)
            messenger.send(message);
    }

    @Override
    public <T> void notifyClient(NotifierMessage<T> message) {
        if (messenger != null) {
            try {
                messenger.send(new ObjectMapper().writeValueAsString(message));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

}
