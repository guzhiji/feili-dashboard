package com.feiliks.dashboard.spring.dto;

import com.feiliks.dashboard.spring.entities.DataSourceEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


public class DataSourceFormDto {

    private Long id;

    @NotNull(message = "data-source-name-empty")
    @NotBlank(message = "data-source-name-empty")
    private String name;

    @NotNull(message = "data-source-internalname-empty")
    @NotBlank(message = "data-source-internalname-empty")
    private String internalName;

    public DataSourceEntity toEntity() {
        DataSourceEntity entity = new DataSourceEntity();
        toEntity(entity);
        return entity;
    }

    public void toEntity(DataSourceEntity entity) {
        entity.setId(id);
        entity.setName(name);
        entity.setInternalName(internalName);
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

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }
}
