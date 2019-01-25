package com.feiliks.dashboard.spring.dto;

import com.feiliks.dashboard.spring.entities.DataSourceEntity;
import com.feiliks.dashboard.spring.entities.MonitorEntity;

public class DataSourceDto {
    private Long id;
    private String name;
    private String internalName;
    private MonitorDto monitor;

    public DataSourceDto() {
    }

    public DataSourceDto(DataSourceEntity entity) {
        setId(entity.getId());
        setName(entity.getName());
        setInternalName(entity.getInternalName());
        MonitorEntity m = entity.getMonitor();
        setMonitor(m == null ? null : new MonitorDto(m));
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

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public MonitorDto getMonitor() {
        return monitor;
    }

    public void setMonitor(MonitorDto monitor) {
        this.monitor = monitor;
    }
}
