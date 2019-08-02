package com.feiliks.dashboard.spring.admin.dto;

import com.feiliks.dashboard.spring.entities.DashboardEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


public class DashboardFormDto {
    private Long id;

    @NotNull(message = "dashboard-pathkey-empty")
    @NotBlank(message = "dashboard-pathkey-empty")
    private String pathKey;

    @NotNull(message = "dashboard-name-empty")
    @NotBlank(message = "dashboard-name-empty")
    private String name;

    private boolean active;

    @NotNull(message = "dashboard-tpl-empty")
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
