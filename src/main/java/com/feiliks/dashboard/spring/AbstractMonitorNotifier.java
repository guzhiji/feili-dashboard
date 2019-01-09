package com.feiliks.dashboard.spring;

import com.feiliks.dashboard.*;

public abstract class AbstractMonitorNotifier implements IMonitor, INotifier {

    private DataSourceStore dataSourceStore = null;
    private IMessenger messenger;
    private INotifierData notifier;
    private IMonitorData monitor;

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
    public void initMonitor(IMonitorData data) {
        monitor = data;
        dataSourceStore = new DataSourceStore();
    }

    @Override
    public IMonitorData getMonitor() {
        return monitor;
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

}
