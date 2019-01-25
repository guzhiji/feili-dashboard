package com.feiliks.dashboard.spring.impl;

import com.feiliks.dashboard.*;

import javax.sql.DataSource;


public abstract class AbstractMonitorNotifier implements IMonitor, INotifier {

    private DataSourceStore dataSourceStore = null;
    private IMessenger messenger;
    private INotifierData notifier;
    private IMonitorData monitor;
    private DataSource database;

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
    public void initMonitor(IMonitorData data, IDbConnManager connManager) {
        monitor = data;
        dataSourceStore = new DataSourceStore();
        IDatabaseInfo db = data.getDatabaseInfo();
        database = db == null ? null : connManager.getDatabase(db);
    }

    @Override
    public IMonitorData getMonitor() {
        return monitor;
    }

    @Override
    public DataSource getDatabase() {
        return database;
    }

    @Override
    public void notifyClient(String message) {
        if (messenger != null)
            messenger.send(message);
    }

    public String retrieveDataSource(String dsName) {
        if (dataSourceStore == null) return "null";
        return dataSourceStore.retrieveDataSourceAsJson(dsName);
    }

    @Override
    public void exportDataSource(String dsName, Object data) {
        if (dataSourceStore != null) {
            try {
                dataSourceStore.store(dsName, data);
            } catch (Exception e) {
                dataSourceStore.clear(dsName);
            }
        }
    }

    @Override
    public void exportDataSourcePreformatted(String dsName, String json) {
        if (dataSourceStore != null) {
            if (json == null)
                exportDataSource(dsName, null);
            else
                dataSourceStore.storePreformatted(dsName, json);
        }
    }

}
