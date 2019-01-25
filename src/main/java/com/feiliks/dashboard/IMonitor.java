package com.feiliks.dashboard;

import javax.sql.DataSource;


public interface IMonitor extends Runnable {

    void initMonitor(IMonitorData data, IDbConnManager connManager);

    IMonitorData getMonitor();

    DataSource getDatabase();

    String retrieveDataSource(String dsName);

    void exportDataSource(String dsName, Object data);
}
