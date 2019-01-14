package com.feiliks.dashboard.spring;

import com.feiliks.dashboard.IMessenger;
import com.feiliks.dashboard.INotifier;
import com.feiliks.dashboard.INotifierData;


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

}
