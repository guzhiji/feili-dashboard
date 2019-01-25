package com.feiliks.dashboard.spring.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feiliks.dashboard.INotifierData;
import com.feiliks.dashboard.spring.entities.MessageNotifierEntity;

import java.util.Collections;
import java.util.Map;


public class NotifierData implements INotifierData {

    private final Long id;
    private final String name;
    private final String javaClass;
    private final boolean isMonitor;
    private final Map configMap;

    public NotifierData(MessageNotifierEntity entity) {
        id = entity.getId();
        name = entity.getName();
        javaClass = entity.getJavaClass();
        isMonitor = entity.isMonitor();

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
    public boolean isMonitor() {
        return isMonitor;
    }

    @Override
    public Object readConfig(String name) {
        return configMap.get(name);
    }
}
