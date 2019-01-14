package com.feiliks.dashboard.spring;

import com.feiliks.dashboard.IMonitor;
import com.feiliks.dashboard.IMonitorData;

public abstract class AbstractMonitor implements IMonitor {

    private DataSourceStore dataSourceStore = null;
    private IMonitorData monitor;

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
