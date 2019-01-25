package com.feiliks.dashboard.spring.dto;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feiliks.dashboard.spring.entities.MessageNotifierEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;


public class MessageNotifierFormDto {

    private Long id;

    @NotNull(message = "notifier-name-empty")
    @NotBlank(message = "notifier-name-empty")
    private String name;

    @NotNull(message = "notifier-javaclass-empty")
    @NotBlank(message = "notifier-javaclass-empty")
    private String javaClass;

    private boolean isMonitor;

    private boolean isBroker;
    private String brokerUri; // tcp://localhost:61613
    private String brokerUser; // admin
    private String brokerPass; // password
    private String brokerDest;

    public MessageNotifierEntity toEntity() {
        MessageNotifierEntity entity = new MessageNotifierEntity();
        toEntity(entity);
        return entity;
    }

    public void toEntity(MessageNotifierEntity entity) {
        entity.setId(getId());
        entity.setName(getName());
        entity.setJavaClass(getJavaClass());
        entity.setMonitor(isMonitor());

        if (isBroker()) {
            Map<String, String> config = new HashMap<>();
            config.put("brokerUri", getBrokerUri());
            config.put("borkerUser", getBrokerUser());
            config.put("brokerPass", getBrokerPass());
            config.put("brokerDest", getBrokerDest());
            try {
                entity.setConfigData(
                        new ObjectMapper().writeValueAsString(config));
            } catch (JsonProcessingException e) {
                entity.setConfigData(null);
            }
        } else {
            entity.setConfigData(null);
        }
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

    public boolean isMonitor() {
        return isMonitor;
    }

    public void setMonitor(boolean monitor) {
        isMonitor = monitor;
    }

    public boolean isBroker() {
        return isBroker;
    }

    public void setBroker(boolean broker) {
        isBroker = broker;
    }

    public String getBrokerUri() {
        return brokerUri;
    }

    public void setBrokerUri(String brokerUri) {
        this.brokerUri = brokerUri;
    }

    public String getBrokerUser() {
        return brokerUser;
    }

    public void setBrokerUser(String brokerUser) {
        this.brokerUser = brokerUser;
    }

    public String getBrokerPass() {
        return brokerPass;
    }

    public void setBrokerPass(String brokerPass) {
        this.brokerPass = brokerPass;
    }

    public String getBrokerDest() {
        return brokerDest;
    }

    public void setBrokerDest(String brokerDest) {
        this.brokerDest = brokerDest;
    }
}
