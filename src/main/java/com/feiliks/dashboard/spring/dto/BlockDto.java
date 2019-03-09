package com.feiliks.dashboard.spring.dto;

import com.feiliks.dashboard.spring.entities.BlockEntity;


public class BlockDto {

    private Long id;
    private String name;
    private String dataRenderer;
    private String dataPreprocessor;
    private String messageHandler;
    private int minHeight;
    private int width;
    private int ordinal;
    private Long monitorId;

    public BlockDto() {}

    public BlockDto(BlockEntity entity) {
        id = entity.getId();
        name = entity.getName();
        dataRenderer = entity.getDataRenderer();
        dataPreprocessor = entity.getDataPreprocessor();
        messageHandler = entity.getMessageHandler();
        minHeight = entity.getMinHeight();
        width = entity.getWidth();
        ordinal = entity.getOrdinal();
        monitorId = entity.getMonitor() == null ? null :
                entity.getMonitor().getId();
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

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public Long getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(Long monitorId) {
        this.monitorId = monitorId;
    }
}
