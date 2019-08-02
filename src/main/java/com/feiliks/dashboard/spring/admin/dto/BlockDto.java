package com.feiliks.dashboard.spring.admin.dto;

import com.feiliks.dashboard.spring.entities.BlockEntity;
import com.feiliks.dashboard.spring.entities.FieldEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;


public class BlockDto {

    private Long id;
    private String name;
    private String dataRenderer;
    private String resultSource;
    private String resultHandler;
    private String messageSource;
    private String messageHandler;
    private int minHeight;
    private int width;
    private int ordinal;
    private Long monitorId;
    private Collection<FieldDto> fields;

    public BlockDto() {}

    public BlockDto(BlockEntity entity) {

        id = entity.getId();
        name = entity.getName();
        dataRenderer = entity.getDataRenderer();
        resultSource = entity.getResultSource();
        resultHandler = entity.getResultHandler();
        messageSource = entity.getMessageSource();
        messageHandler = entity.getMessageHandler();
        minHeight = entity.getMinHeight();
        width = entity.getWidth();
        ordinal = entity.getOrdinal();

        monitorId = entity.getMonitor() == null ? null :
                entity.getMonitor().getId();

        List<FieldDto> fl = new ArrayList<>(entity.getFields().size());
        for (FieldEntity fe : entity.getFields()) {
            if (!fe.isActive()) continue;
            fl.add(new FieldDto(fe));
        }
        fl.sort(Comparator.comparingInt(FieldDto::getOrdinal));
        fields = fl;

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

    public Collection<FieldDto> getFields() {
        return fields;
    }

    public void setFields(Collection<FieldDto> fields) {
        this.fields = fields;
    }
}
