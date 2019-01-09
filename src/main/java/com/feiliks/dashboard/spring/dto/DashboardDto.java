package com.feiliks.dashboard.spring.dto;

import com.feiliks.dashboard.spring.entities.BlockEntity;
import com.feiliks.dashboard.spring.entities.DashboardEntity;

import java.util.ArrayList;
import java.util.Collection;

public class DashboardDto {

    private Long id;
    private String pathKey;
    private String name;
    private boolean active;
    private TemplateDto template;
    private Collection<BlockDto> blocks;

    public DashboardDto() {}

    public DashboardDto(DashboardEntity entity) {
        setId(entity.getId());
        setPathKey(entity.getPathKey());
        setName(entity.getName());
        setActive(entity.isActive());
        setTemplate(new TemplateDto(entity.getTemplate()));

        ArrayList<BlockDto> blist = new ArrayList<>();
        for (BlockEntity be : entity.getBlocks())
            blist.add(new BlockDto(be));
        setBlocks(blist);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPathKey() {
        return pathKey;
    }

    public void setPathKey(String pathKey) {
        this.pathKey = pathKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public TemplateDto getTemplate() {
        return template;
    }

    public void setTemplate(TemplateDto template) {
        this.template = template;
    }

    public Collection<BlockDto> getBlocks() {
        return blocks;
    }

    public void setBlocks(Collection<BlockDto> blocks) {
        this.blocks = blocks;
    }
}
