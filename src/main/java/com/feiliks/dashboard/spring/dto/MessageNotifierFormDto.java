package com.feiliks.dashboard.spring.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feiliks.dashboard.spring.entities.MessageNotifierEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
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

    public MessageNotifierFormDto() {
    }

    public MessageNotifierFormDto(MessageNotifierEntity entity) {
        id = entity.getId();
        name = entity.getName();
        javaClass = entity.getJavaClass();
        isMonitor = entity.isMonitor();

        // parse configData to read message broker info
        if (entity.getConfigData() == null) {
            isBroker = false;
            brokerUri = null;
            brokerUser = null;
            brokerPass = null;
            brokerDest = null;
        } else {
            try {
                Map config = new ObjectMapper().readValue(
                        entity.getConfigData(), Map.class);
                brokerUri = (String) config.get("brokerUri");
                isBroker = brokerUri != null;
                brokerUser = (String) config.get("brokerUser");
                brokerPass = (String) config.get("brokerPass");
                brokerDest = (String) config.get("brokerDest");
            } catch (IOException ignored) {
                isBroker = false;
                brokerUri = null;
                brokerUser = null;
                brokerPass = null;
                brokerDest = null;
            }
        }
    }

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

        // save message broker info as JSON into configData
        if (isBroker()) {
            Map<String, String> config = new HashMap<>();
            config.put("brokerUri", getBrokerUri());
            config.put("brokerUser", getBrokerUser());
            config.put("brokerPass", getBrokerPass());
            config.put("brokerDest", getBrokerDest());
            try {
                entity.setConfigData(
                        new ObjectMapper().writeValueAsString(config));
            } catch (JsonProcessingException ignored) {
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
