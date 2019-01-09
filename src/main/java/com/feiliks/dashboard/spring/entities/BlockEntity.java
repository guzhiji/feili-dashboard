package com.feiliks.dashboard.spring.entities;

import javax.persistence.*;

@Entity
@Table(name = "dashboard_block")
public class BlockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String name;

    @ManyToOne(optional = false)
    private DashboardEntity dashboard;

    @Column(name = "data_renderer", nullable = false, length = 32)
    private String dataRenderer;

    @Column(name = "data_preprocessor", length = 32)
    private String dataPreprocessor;

    @ManyToOne
    @JoinColumn(name = "data_source")
    private DataSourceEntity dataSource;

    @Column(name = "message_handler", length = 32)
    private String messageHandler;

    @ManyToOne
    @JoinColumn(name = "message_notifier")
    private MessageNotifierEntity messageNotifier;

    @Column(name = "min_height", nullable = false)
    private int minHeight;

    @Column(nullable = false)
    private int width;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(nullable = false)
    private int ordinal;

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

    public DashboardEntity getDashboard() {
        return dashboard;
    }

    public void setDashboard(DashboardEntity dashboard) {
        this.dashboard = dashboard;
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

    public DataSourceEntity getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceEntity dataSource) {
        this.dataSource = dataSource;
    }

    public MessageNotifierEntity getMessageNotifier() {
        return messageNotifier;
    }

    public void setMessageNotifier(MessageNotifierEntity messageNotifier) {
        this.messageNotifier = messageNotifier;
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

