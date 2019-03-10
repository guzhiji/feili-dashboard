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

    @NotNull(message = "block-result-source-empty")
    @NotBlank(message = "block-result-source-empty")
    private String resultSource;
    private String resultHandler;

    private String messageSource;
    private String messageHandler;

    @NotNull(message = "monitor-required")
    private Long monitorId;

    @NotNull(message = "block-min-height-required")
    private Integer minHeight;

    @NotNull(message = "block-width-required")
    private Integer width;

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
        entity.setResultSource(resultSource);
        entity.setResultHandler(resultHandler);
        entity.setMessageSource(messageSource);
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

    public String getResultHandler() {
        return resultHandler;
    }

    public void setResultHandler(String resultHandler) {
        this.resultHandler = resultHandler;
    }

    public String getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(String messageHandler) {
        this.messageHandler = messageHandler;
    }

    public Integer getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(Integer minHeight) {
        this.minHeight = minHeight;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
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

    public String getResultSource() {
        return resultSource;
    }

    public void setResultSource(String resultSource) {
        this.resultSource = resultSource;
    }

    public String getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(String messageSource) {
        this.messageSource = messageSource;
    }
}
