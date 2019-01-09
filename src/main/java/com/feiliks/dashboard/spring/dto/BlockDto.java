package com.feiliks.dashboard.spring.dto;

import com.feiliks.dashboard.spring.entities.BlockEntity;

public class BlockDto {

    private Long id;
    private String name;
    private MonitorDto monitor;
    private String dataRenderer;
    private String dataPreprocessor;
    private String notificationHandler;
    private int minHeight;
    private int width;

    public BlockDto() {}

    public BlockDto(BlockEntity entity) {
        id = entity.getId();
        name = entity.getName();
        monitor = new MonitorDto(entity.getDataSource().getMonitor());
        dataRenderer = entity.getDataRenderer();
        dataPreprocessor = entity.getDataPreprocessor();
        notificationHandler = entity.getMessageHandler();
        minHeight = entity.getMinHeight();
        width = entity.getWidth();
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

    public MonitorDto getMonitor() {
        return monitor;
    }

    public void setMonitor(MonitorDto monitor) {
        this.monitor = monitor;
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

    public String getNotificationHandler() {
        return notificationHandler;
    }

    public void setNotificationHandler(String notificationHandler) {
        this.notificationHandler = notificationHandler;
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
}
