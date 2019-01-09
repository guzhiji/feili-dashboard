package com.feiliks.dashboard.spring.dto;

import com.feiliks.dashboard.spring.entities.DashboardEntity;

public class DashboardFormDto {
    private Long id;
    private String pathKey;
    private String name;
    private boolean active;
    private Long templateId;

    public DashboardEntity toEntity() {
        DashboardEntity entity = new DashboardEntity();
        toEntity(entity);
        return entity;
    }

    public void toEntity(DashboardEntity entity) {
        entity.setId(id);
        entity.setPathKey(pathKey);
        entity.setName(name);
        entity.setActive(active);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPathKey() {
        return pathKey;
    }

    public void setPathKey(String pathKey) {
        this.pathKey = pathKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }
}
