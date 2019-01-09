package com.feiliks.dashboard.spring;

import com.feiliks.dashboard.IMonitorData;
import com.feiliks.dashboard.spring.entities.MonitorEntity;

public class MonitorData implements IMonitorData {

    private final Long id;
    private final String name;
    private final String javaClass;
    private final String args;
    private final long execRate;

    public MonitorData(MonitorEntity entity) {
        id = entity.getId();
        name = entity.getName();
        javaClass = entity.getJavaClass();
        args = entity.getArgs();
        execRate = entity.getExecRate();
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
    public String getArgs() {
        return args;
    }

    @Override
    public long getExecRate() {
        return execRate;
    }

}
