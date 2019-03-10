package com.feiliks.dashboard.spring.entities;

import javax.persistence.*;
import java.util.Collection;


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

    @OneToMany(mappedBy = "block", cascade = CascadeType.ALL)
    private Collection<FieldEntity> fields;

    @Column(name = "data_renderer", nullable = false, length = 32)
    private String dataRenderer;

    @ManyToOne(optional = false)
    private MonitorEntity monitor;

    @Column(name = "result_source", nullable = false, length = 32)
    private String resultSource;

    @Column(name = "result_handler", length = 32)
    private String resultHandler;

    @Column(name = "message_source", length = 32)
    private String messageSource;

    @Column(name = "message_handler", length = 32)
    private String messageHandler;

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

    public String getResultHandler() {
        return resultHandler;
    }

    public void setResultHandler(String resultHandler) {
        this.resultHandler = resultHandler;
    }

    public MonitorEntity getMonitor() {
        return monitor;
    }

    public void setMonitor(MonitorEntity monitor) {
        this.monitor = monitor;
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

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
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

    public Collection<FieldEntity> getFields() {
        return fields;
    }

    public void setFields(Collection<FieldEntity> fields) {
        this.fields = fields;
    }
}

