package com.feiliks.dashboard.spring;

import com.feiliks.dashboard.INotifierData;
import com.feiliks.dashboard.spring.entities.MessageNotifierEntity;

public class NotifierData implements INotifierData {

    private final Long id;
    private final String name;
    private final boolean isMonitor;
    private final String javaClass;

    public NotifierData(MessageNotifierEntity entity) {
        id = entity.getId();
        name = entity.getName();
        isMonitor = entity.isMonitor();
        javaClass = entity.getJavaClass();
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
    public boolean isMonitor() {
        return isMonitor;
    }

    @Override
    public String getJavaClass() {
        return javaClass;
    }
}
