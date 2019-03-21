package com.feiliks.dashboard.spring.dto;

import com.feiliks.dashboard.spring.entities.FieldEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


public class FieldDto {
    private Long id;

    @NotNull(message = "field-name-empty")
    @NotBlank(message = "field-name-empty")
    private String name;

    @NotNull(message = "field-internal-name-empty")
    @NotBlank(message = "field-internal-name-empty")
    private String internalName;

    private String valueTransformer;
    private String valueFormatter;
    private boolean active;
    private int ordinal;

    public FieldDto() {}

    public FieldDto(FieldEntity entity) {
        id = entity.getId();
        name = entity.getName();
        internalName = entity.getInternalName();
        setValueTransformer(entity.getValueTransformer());
        valueFormatter = entity.getValueFormatter();
        active = entity.isActive();
        ordinal = entity.getOrdinal();
    }

    public void toEntity(FieldEntity entity) {
        entity.setId(id);
        entity.setName(name);
        entity.setInternalName(internalName);
        entity.setValueTransformer(getValueTransformer());
        entity.setValueFormatter(valueFormatter);
        entity.setActive(active);
        entity.setOrdinal(ordinal);
    }

    public FieldEntity toEntity() {
        FieldEntity entity = new FieldEntity();
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

    public String getValueTransformer() {
        return valueTransformer;
    }

    public void setValueTransformer(String valueTransformer) {
        this.valueTransformer = valueTransformer;
    }

    public String getValueFormatter() {
        return valueFormatter;
    }

    public void setValueFormatter(String valueFormatter) {
        this.valueFormatter = valueFormatter;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

}
