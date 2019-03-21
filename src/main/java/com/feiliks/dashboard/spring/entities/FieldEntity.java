package com.feiliks.dashboard.spring.entities;

import javax.persistence.*;


@Entity
@Table(name = "dashboard_field")
public class FieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String name;

    @Column(name = "internal_name", nullable = false, length = 32)
    private String internalName; // TODO refAttribute

    @Column(name = "value_transformer", length = 32)
    private String valueTransformer;

    @Column(name = "value_formatter", length = 32)
    private String valueFormatter;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(nullable = false)
    private int ordinal;

    @ManyToOne(optional = false)
    private BlockEntity block;

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

    public String getValueTransformer() {
        return valueTransformer;
    }

    public void setValueTransformer(String valueTransformer) {
        this.valueTransformer = valueTransformer;
    }

    public String getValueFormatter() {
        return valueFormatter;
    }

    public void setValueFormatter(String valueFormatter) {
        this.valueFormatter = valueFormatter;
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

    public BlockEntity getBlock() {
        return block;
    }

    public void setBlock(BlockEntity block) {
        this.block = block;
    }

}
