package com.feiliks.dashboard.spring.dto;

import com.feiliks.dashboard.spring.entities.MonitorEntity;

public class MonitorDto {
    private Long id;
    private String name;

    public MonitorDto() {}

    public MonitorDto(MonitorEntity entity) {
        id = entity.getId();
        name = entity.getName();
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
}
