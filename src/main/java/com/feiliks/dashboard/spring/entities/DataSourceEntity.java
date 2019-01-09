package com.feiliks.dashboard.spring.entities;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "dashboard_datasource")
public class DataSourceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String name;

    @Column(name = "internal_name", nullable = false, length = 32)
    private String internalName;

    @ManyToOne(optional = false)
    private MonitorEntity monitor;

    @OneToMany(mappedBy = "dataSource")
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

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public MonitorEntity getMonitor() {
        return monitor;
    }

    public void setMonitor(MonitorEntity monitor) {
        this.monitor = monitor;
    }

    public Collection<BlockEntity> getBlocks() {
        return blocks;
    }

    public void setBlocks(Collection<BlockEntity> blocks) {
        this.blocks = blocks;
    }

}
