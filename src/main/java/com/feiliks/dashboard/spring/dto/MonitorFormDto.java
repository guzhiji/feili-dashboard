package com.feiliks.dashboard.spring.dto;

import com.feiliks.dashboard.spring.entities.MonitorEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


public class MonitorFormDto {

    private Long id;

    @NotNull(message = "monitor-name-empty")
    @NotBlank(message = "monitor-name-empty")
    private String name;

    @NotNull(message = "monitor-javaclass-empty")
    @NotBlank(message = "monitor-javaclass-empty")
    private String javaClass;

    private String args;

    @NotNull(message = "monitor-execrate-empty")
    private Long execRate;

    public MonitorEntity toEntity() {
        MonitorEntity entity = new MonitorEntity();
        toEntity(entity);
        return entity;
    }

    public void toEntity(MonitorEntity entity) {
        entity.setId(id);
        entity.setName(name);
        entity.setJavaClass(javaClass);
        entity.setArgs(args);
        entity.setExecRate(execRate);
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

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public Long getExecRate() {
        return execRate;
    }

    public void setExecRate(Long execRate) {
        this.execRate = execRate;
    }
}
