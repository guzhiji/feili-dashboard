package com.feiliks.dashboard.spring.dto;

import com.feiliks.dashboard.spring.entities.MonitorEntity;

import java.util.Map;

public class MonitorDto {

    private Long id;
    private String name;
    private long execRate;
    private Map<String, String> resultSources;
    private Map<String, String> notificationSources;

    public MonitorDto() {}

    public MonitorDto(MonitorEntity entity) {
        setId(entity.getId());
        setName(entity.getName());
        setExecRate(entity.getExecRate());
        setResultSources(null);
        setNotificationSources(null);
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

    public long getExecRate() {
        return execRate;
    }

    public void setExecRate(long execRate) {
        this.execRate = execRate;
    }

    public Map<String, String> getResultSources() {
        return resultSources;
    }

    public void setResultSources(Map<String, String> resultSources) {
        this.resultSources = resultSources;
    }

    public Map<String, String> getNotificationSources() {
        return notificationSources;
    }

    public void setNotificationSources(Map<String, String> notificationSources) {
        this.notificationSources = notificationSources;
    }
}
