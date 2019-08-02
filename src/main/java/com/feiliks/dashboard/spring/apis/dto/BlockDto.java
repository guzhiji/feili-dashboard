package com.feiliks.dashboard.spring.apis.dto;

import com.feiliks.dashboard.spring.entities.BlockEntity;

public class BlockDto {
    private Long id;
    private String name;
    private Long dashboardId;
    private String dataRenderer;
    private Long monitorId;
    private String resultSource;
    private String resultHandler;
    private String messageSource;
    private String messageHandler;
    private int minHeight;
    private int width;
    private Boolean active;
    private int ordinal;

    public BlockDto() {
    }

    public BlockDto(BlockEntity e) {
        id = e.getId();
        name = e.getName();
        dashboardId = e.getDashboard() == null ? null :
                e.getDashboard().getId();
        dataRenderer = e.getDataRenderer();
        monitorId = e.getMonitor() == null ? null :
                e.getMonitor().getId();
        resultSource = e.getResultSource();
        resultHandler = e.getResultHandler();
        messageSource = e.getMessageSource();
        messageHandler = e.getMessageHandler();
        minHeight = e.getMinHeight();
        width = e.getWidth();
        active = e.isActive();
        ordinal = e.getOrdinal();
    }

    public void toEntity(BlockEntity e) {
        e.setName(name);
        e.setDataRenderer(dataRenderer);
        e.setResultSource(resultSource);
        e.setResultHandler(resultHandler);
        e.setMessageSource(messageSource);
        e.setMessageHandler(messageHandler);
        e.setMinHeight(minHeight);
        e.setWidth(width);
        e.setActive(active == null ? true : active);
        e.setOrdinal(ordinal);
    }

    public BlockEntity toEntity() {
        BlockEntity e = new BlockEntity();
        toEntity(e);
        return e;
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

    public Long getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(Long dashboardId) {
        this.dashboardId = dashboardId;
    }

    public String getDataRenderer() {
        return dataRenderer;
    }

    public void setDataRenderer(String dataRenderer) {
        this.dataRenderer = dataRenderer;
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

    public String getResultHandler() {
        return resultHandler;
    }

    public void setResultHandler(String resultHandler) {
        this.resultHandler = resultHandler;
    }

    public String getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(String messageSource) {
        this.messageSource = messageSource;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (id == null) return false;
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockDto blockDto = (BlockDto) o;
        return id.equals(blockDto.id);
    }

}
