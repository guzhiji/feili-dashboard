package com.feiliks.dashboard.spring.apis.dto;

import com.feiliks.dashboard.spring.entities.MonitorEntity;

public class MonitorDto {
    private Long id;
    private String name;
    private String javaClass;
    private long execRate;
    private Long databaseId;
    private String configData;

    public MonitorDto() {
    }

    public MonitorDto(MonitorEntity monitor) {
        this.id = monitor.getId();
        this.name = monitor.getName();
        this.javaClass = monitor.getJavaClass();
        this.execRate = monitor.getExecRate();
        this.databaseId = monitor.getDatabase() == null ? null :
                monitor.getDatabase().getId();
        this.configData = monitor.getConfigData();
    }

    public void toEntity(MonitorEntity e) {
        e.setName(name);
        e.setJavaClass(javaClass);
        e.setExecRate(execRate);
        e.setConfigData(configData);
    }

    public MonitorEntity toEntity() {
        MonitorEntity e = new MonitorEntity();
        toEntity(e);
        return e;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJavaClass() {
        return javaClass;
    }

    public void setJavaClass(String javaClass) {
        this.javaClass = javaClass;
    }

    public long getExecRate() {
        return execRate;
    }

    public void setExecRate(long execRate) {
        this.execRate = execRate;
    }

    public Long getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Long databaseId) {
        this.databaseId = databaseId;
    }

    public String getConfigData() {
        return configData;
    }

    public void setConfigData(String configData) {
        this.configData = configData;
    }
}
