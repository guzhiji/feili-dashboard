package com.feiliks.dashboard.spring.dto;

import com.feiliks.dashboard.spring.entities.BlockEntity;

public class BlockFormDto {
    private Long id;
    private String name;
    private String dataRenderer;
    private String dataPreprocessor;
    private Long dataSourceId;
    private String messageHandler;
    private Long messageNotifierId;
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

    public Long getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public String getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(String messageHandler) {
        this.messageHandler = messageHandler;
    }

    public Long getMessageNotifierId() {
        return messageNotifierId;
    }

    public void setMessageNotifierId(Long messageNotifierId) {
        this.messageNotifierId = messageNotifierId;
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
}
