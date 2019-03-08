package com.feiliks.dashboard.spring.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feiliks.dashboard.spring.entities.DatabaseEntity;
import com.feiliks.dashboard.spring.entities.MonitorEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
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

    private Long databaseId;

    private String dbSql;

    public MonitorFormDto() {
    }

    public MonitorFormDto(MonitorEntity entity) {
        id = entity.getId();
        name = entity.getName();
        javaClass = entity.getJavaClass();
        execRate = entity.getExecRate();
        DatabaseEntity db = entity.getDatabase();
        databaseId = db == null ? null : db.getId();
        // parse configData to read dbSql
        if (entity.getConfigData() == null) {
            dbSql = null;
        } else {
            try {
                Map config = new ObjectMapper().readValue(
                        entity.getConfigData(), Map.class);
                dbSql = (String) config.get("dbSql");
            } catch (IOException ignored) {
                dbSql = null;
            }
        }
    }

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
        // databaseId needs custom code to fetch DatabaseEntity object

        // save dbSql as JSON into configData
        if (dbSql != null && !dbSql.trim().isEmpty()) {
            Map<String, String> config = new HashMap<>();
            config.put("dbSql", dbSql.trim());
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

    public Long getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Long databaseId) {
        this.databaseId = databaseId;
    }

    public String getDbSql() {
        return dbSql;
    }

    public void setDbSql(String dbSql) {
        this.dbSql = dbSql;
    }
}

