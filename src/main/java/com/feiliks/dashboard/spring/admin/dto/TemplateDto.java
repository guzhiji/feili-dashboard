package com.feiliks.dashboard.spring.admin.dto;

import com.feiliks.dashboard.spring.entities.TemplateEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


public class TemplateDto {

    private Long id;

    @NotNull(message = "template-name-empty")
    @NotBlank(message = "template-name-empty")
    private String name;

    @NotNull(message = "template-internalname-empty")
    @NotBlank(message = "template-internalname-empty")
    private String internalName;

    public TemplateDto() {
    }

    public TemplateDto(TemplateEntity entity) {
        id = entity.getId();
        name = entity.getName();
        internalName = entity.getInternalName();
    }

    public void toEntity(TemplateEntity entity) {
        entity.setId(id);
        entity.setName(name);
        entity.setInternalName(internalName);
    }

    public TemplateEntity toEntity() {
        TemplateEntity entity = new TemplateEntity();
        toEntity(entity);
        return entity;
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
