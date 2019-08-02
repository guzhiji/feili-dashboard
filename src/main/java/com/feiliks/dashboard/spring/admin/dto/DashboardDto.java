package com.feiliks.dashboard.spring.admin.dto;

import com.feiliks.dashboard.spring.entities.BlockEntity;
import com.feiliks.dashboard.spring.entities.DashboardEntity;

import java.util.*;

public class DashboardDto {

    private Long id;
    private String pathKey;
    private String name;
    private boolean active;
    private TemplateDto template;
    private Collection<BlockDto> blocks;
    private Collection<MonitorDto> monitors;

    public DashboardDto() {}

    public DashboardDto(DashboardEntity entity) {
        setId(entity.getId());
        setPathKey(entity.getPathKey());
        setName(entity.getName());
        setActive(entity.isActive());
        setTemplate(new TemplateDto(entity.getTemplate()));

        Map<Long, MonitorDto> mmap = new HashMap<>();
        ArrayList<BlockDto> blist = new ArrayList<>(entity.getBlocks().size());
        for (BlockEntity be : entity.getBlocks()) {
            if (!be.isActive()) continue;
            blist.add(new BlockDto(be));
            Long mid = be.getMonitor() == null ? null :
                    be.getMonitor().getId();
            if (mid != null && !mmap.containsKey(mid))
                mmap.put(mid, new MonitorDto(be.getMonitor()));
        }
        blist.sort(Comparator.comparing(BlockDto::getOrdinal));
        setBlocks(blist);
        setMonitors(mmap.values());
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

    public Collection<MonitorDto> getMonitors() {
        return monitors;
    }

    public void setMonitors(Collection<MonitorDto> monitors) {
        this.monitors = monitors;
    }
}
