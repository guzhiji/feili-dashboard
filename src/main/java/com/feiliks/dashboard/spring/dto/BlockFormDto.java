package com.feiliks.dashboard.spring.dto;

import com.feiliks.dashboard.spring.entities.BlockEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


public class BlockFormDto {
    private Long id;

    @NotNull(message = "block-name-empty")
    @NotBlank(message = "block-name-empty")
    private String name;

    @NotNull(message = "block-renderer-empty")
    @NotBlank(message = "block-renderer-empty")
    private String dataRenderer;

    private String dataPreprocessor;
    private String messageHandler;

    @NotNull(message = "monitor-required")
    private Long monitorId;

    private int minHeight;
    private int width;
    private boolean active;

    public BlockEntity toEntity() {
        BlockEntity entity = new BlockEntity();
        toEntity(entity);
        return entity;
    }

    public void toEntity(BlockEntity entity) {
        entity.setId(id);
        entity.setName(name);
        entity.setDataRenderer(dataRenderer);
        entity.setDataPreprocessor(dataPreprocessor);
        entity.setMessageHandler(messageHandler);
        entity.setMinHeight(minHeight);
        entity.setWidth(width);
        entity.setActive(active);
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

    public String getDataRenderer() {
        return dataRenderer;
    }

    public void setDataRenderer(String dataRenderer) {
        this.dataRenderer = dataRenderer;
    }

    public String getDataPreprocessor() {
        return dataPreprocessor;
    }

    public void setDataPreprocessor(String dataPreprocessor) {
        this.dataPreprocessor = dataPreprocessor;
    }

    public String getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(String messageHandler) {
        this.messageHandler = messageHandler;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(int minHeight) {
        this.minHeight = minHeight;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(Long monitorId) {
        this.monitorId = monitorId;
    }
}
