package com.feiliks.dashboard.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feiliks.dashboard.IMonitorData;
import com.feiliks.dashboard.spring.entities.MonitorEntity;

import java.util.Collections;
import java.util.Map;


public class MonitorData implements IMonitorData {

    private final Long id;
    private final String name;
    private final String javaClass;
    private final long execRate;
    private final Map configMap;

    public MonitorData(MonitorEntity entity) {
        id = entity.getId();
        name = entity.getName();
        javaClass = entity.getJavaClass();
        execRate = entity.getExecRate();

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
    public Object readConfig(String name) {
        return configMap.get(name);
    }
}
