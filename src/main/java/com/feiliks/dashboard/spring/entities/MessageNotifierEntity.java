package com.feiliks.dashboard.spring.entities;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "dashboard_notifier")
public class MessageNotifierEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String name;

    @Column(name = "java_class", nullable = false)
    private String javaClass;

    @Column(name = "is_monitor", nullable = false)
    private boolean isMonitor;

    @Column(name = "config_data")
    private String configData;

    @OneToMany(mappedBy = "messageNotifier")
    private Collection<BlockEntity> blocks;

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

    public boolean isMonitor() {
        return isMonitor;
    }

    public void setMonitor(boolean monitor) {
        isMonitor = monitor;
    }

    public String getJavaClass() {
        return javaClass;
    }

    public void setJavaClass(String javaClass) {
        this.javaClass = javaClass;
    }

    public String getConfigData() {
        return configData;
    }

    public void setConfigData(String configData) {
        this.configData = configData;
    }

    public Collection<BlockEntity> getBlocks() {
        return blocks;
    }

    public void setBlocks(Collection<BlockEntity> blocks) {
        this.blocks = blocks;
    }

    @Override
    public int hashCode() {
        if (id == null)
            return 0;
        return id.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (id == null)
            return this == other;
        if (other instanceof MessageNotifierEntity) {
            MessageNotifierEntity entity = (MessageNotifierEntity) other;
            return id.equals(entity.id);
        }
        return false;
    }

}
