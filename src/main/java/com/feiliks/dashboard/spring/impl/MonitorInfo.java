package com.feiliks.dashboard.spring.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feiliks.dashboard.spring.entities.DatabaseEntity;
import com.feiliks.dashboard.spring.entities.MonitorEntity;
import com.feiliks.dashboard.IDatabaseInfo;
import com.feiliks.dashboard.IMonitorInfo;

import java.util.Collections;
import java.util.Map;


public class MonitorInfo implements IMonitorInfo {

    private final Long id;
    private final String name;
    private final String javaClass;
    private final long execRate;
    private final IDatabaseInfo databaseInfo;
    private final Map configMap;

    public MonitorInfo(MonitorEntity entity) {
        id = entity.getId();
        name = entity.getName();
        javaClass = entity.getJavaClass();
        execRate = entity.getExecRate();

        DatabaseEntity database = entity.getDatabase();
        if (database == null) {
            databaseInfo = null;
        } else {
            databaseInfo = new DatabaseInfo(database);
        }

        String configData = entity.getConfigData();
        Map m;
        try {
            m = new ObjectMapper().readValue(configData, Map.class);
        } catch (Exception e) {
            m = Collections.EMPTY_MAP;
        }
        configMap = m;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getJavaClass() {
        return javaClass;
    }

    @Override
    public long getExecRate() {
        return execRate;
    }

    @Override
    public IDatabaseInfo getDatabaseInfo() {
        return databaseInfo;
    }

    @Override
    public Object readConfig(String name) {
        return configMap.get(name);
    }

}
