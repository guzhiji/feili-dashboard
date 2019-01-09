package com.feiliks.dashboard;

public interface IMonitor extends Runnable {

    void initMonitor(IMonitorData data);

    IMonitorData getMonitor();

    String retrieveDataSource(String dsName);

    void exportDataSource(String dsName, Object data);
}
