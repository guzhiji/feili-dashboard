package com.feiliks.dashboard.spring.apis.dto;

import com.feiliks.dashboard.spring.entities.DashboardEntity;

public class DashboardDto {
    private Long id;
    private String pathKey;
    private String name;
    private Boolean active;
    private Long templateId;

    public DashboardDto() {
    }

    public DashboardDto(DashboardEntity e) {
        id = e.getId();
        pathKey = e.getPathKey();
        name = e.getName();
        active = e.isActive();
        templateId = e.getTemplate() == null ? null :
                e.getTemplate().getId();
    }

    public void toEntity(DashboardEntity e) {
        e.setPathKey(pathKey);
        e.setName(name);
        e.setActive(active == null ? false : active);
    }

    public DashboardEntity toEntity() {
        DashboardEntity e = new DashboardEntity();
        toEntity(e);
        return e;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

}
