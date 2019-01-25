package com.feiliks.dashboard.spring.dto;

import com.feiliks.dashboard.spring.entities.MessageNotifierEntity;


public class MessageNotifierDto {
    private Long id;
    private String name;
    private boolean isMonitor;

    public MessageNotifierDto() {
    }

    public MessageNotifierDto(MessageNotifierEntity entity) {
        setId(entity.getId());
        setName(entity.getName());
        setMonitor(entity.isMonitor());
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

    public boolean isMonitor() {
        return isMonitor;
    }

    public void setMonitor(boolean monitor) {
        isMonitor = monitor;
    }
}
