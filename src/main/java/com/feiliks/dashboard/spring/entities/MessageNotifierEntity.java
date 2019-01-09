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

    @Column(name = "broker_dest", nullable = false, length = 32)
    private String brokerDestination;

    @Column(name = "is_monitor", nullable = false)
    private boolean isMonitor;

    @Column(name = "java_class", nullable = false)
    private String javaClass;

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

    public String getBrokerDestination() {
        return brokerDestination;
    }

    public void setBrokerDestination(String brokerDestination) {
        this.brokerDestination = brokerDestination;
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

    public Collection<BlockEntity> getBlocks() {
        return blocks;
    }

    public void setBlocks(Collection<BlockEntity> blocks) {
        this.blocks = blocks;
    }
}
