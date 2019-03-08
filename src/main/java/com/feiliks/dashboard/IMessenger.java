package com.feiliks.dashboard;

public interface IMessenger {
    void send(long monitorId, String source, String message);
}
