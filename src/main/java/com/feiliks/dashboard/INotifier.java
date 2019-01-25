package com.feiliks.dashboard;


public interface INotifier extends Runnable {

    void initNotifier(INotifierData data, IMessenger messenger);

    INotifierData getNotifier();

    void notifyClient(String message);
}
