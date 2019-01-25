package com.feiliks.dashboard.spring.dto;

import com.feiliks.dashboard.spring.entities.BlockEntity;
import com.feiliks.dashboard.spring.entities.DataSourceEntity;
import com.feiliks.dashboard.spring.entities.MessageNotifierEntity;

public class BlockDto {

    private Long id;
    private String name;
    private DataSourceDto dataSource;
    private MessageNotifierDto messageNotifier;
    private String dataRenderer;
    private String dataPreprocessor;
    private String notificationHandler;
    private int minHeight;
    private int width;

    public BlockDto() {}

    public BlockDto(BlockEntity entity) {
        id = entity.getId();
        name = entity.getName();
        DataSourceEntity dse = entity.getDataSource();
        setDataSource(dse == null ? null : new DataSourceDto(dse));
        MessageNotifierEntity mne = entity.getMessageNotifier();
        setMessageNotifier(mne == null ? null : new MessageNotifierDto(mne));
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

    public DataSourceDto getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceDto dataSource) {
        this.dataSource = dataSource;
    }

    public MessageNotifierDto getMessageNotifier() {
        return messageNotifier;
    }

    public void setMessageNotifier(MessageNotifierDto messageNotifier) {
        this.messageNotifier = messageNotifier;
    }
}
