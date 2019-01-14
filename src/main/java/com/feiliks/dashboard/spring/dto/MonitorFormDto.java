package com.feiliks.dashboard.spring.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feiliks.dashboard.spring.entities.MonitorEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;


public class MonitorFormDto {

    private Long id;

    @NotNull(message = "monitor-name-empty")
    @NotBlank(message = "monitor-name-empty")
    private String name;

    @NotNull(message = "monitor-javaclass-empty")
    @NotBlank(message = "monitor-javaclass-empty")
    private String javaClass;

    @NotNull(message = "monitor-execrate-empty")
    private Long execRate;

    private boolean isDb;
    private String dbUri;
    private String dbUser;
    private String dbPass;
    private String dbSql;

    public MonitorEntity toEntity() {
        MonitorEntity entity = new MonitorEntity();
        toEntity(entity);
        return entity;
    }

    public void toEntity(MonitorEntity entity) {
        entity.setId(id);
        entity.setName(name);
        entity.setJavaClass(javaClass);
        entity.setExecRate(execRate);

        if (isDb()) {
            Map<String, String> config = new HashMap<>();
            config.put("dbUri", getDbUri());
            config.put("dbUser", getDbUser());
            config.put("dbPass", getDbPass());
            config.put("dbSql", getDbSql());
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

    public Long getExecRate() {
        return execRate;
    }

    public void setExecRate(Long execRate) {
        this.execRate = execRate;
    }

    public boolean isDb() {
        return isDb;
    }

    public void setDb(boolean db) {
        isDb = db;
    }

    public String getDbUri() {
        return dbUri;
    }

    public void setDbUri(String dbUri) {
        this.dbUri = dbUri;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPass() {
        return dbPass;
    }

    public void setDbPass(String dbPass) {
        this.dbPass = dbPass;
    }

    public String getDbSql() {
        return dbSql;
    }

    public void setDbSql(String dbSql) {
        this.dbSql = dbSql;
    }
}
