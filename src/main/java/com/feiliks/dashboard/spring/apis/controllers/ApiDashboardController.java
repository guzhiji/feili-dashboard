package com.feiliks.dashboard.spring.apis.controllers;

import com.feiliks.dashboard.spring.NotFoundException;
import com.feiliks.dashboard.spring.TaskActivationException;
import com.feiliks.dashboard.spring.apis.dto.BlockDto;
import com.feiliks.dashboard.spring.apis.dto.DashboardDto;
import com.feiliks.dashboard.spring.entities.BlockEntity;
import com.feiliks.dashboard.spring.entities.DashboardEntity;
import com.feiliks.dashboard.spring.entities.MonitorEntity;
import com.feiliks.dashboard.spring.entities.TemplateEntity;
import com.feiliks.dashboard.spring.repositories.BlockRepo;
import com.feiliks.dashboard.spring.repositories.DashboardRepo;
import com.feiliks.dashboard.spring.repositories.MonitorRepo;
import com.feiliks.dashboard.spring.repositories.TemplateRepo;
import com.feiliks.dashboard.spring.services.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/apis/dashboards")
public class ApiDashboardController {

    @Autowired
    private DashboardRepo dashRepo;

    @Autowired
    private TemplateRepo tplRepo;

    @Autowired
    private MonitorRepo monitorRepo;

    @Autowired
    private BlockRepo blockRepo;

    @Autowired
    private MonitorService monitorService;

    @GetMapping
    public List<DashboardDto> list() {
        return dashRepo.findAllByOrderByName().stream()
                .map(DashboardDto::new)
                .collect(Collectors.toList());
    }

    @PostMapping
    public DashboardDto create(@RequestBody DashboardDto data) {
        TemplateEntity tplEntity = tplRepo.findById(data.getTemplateId())
                .orElseThrow(() -> new IllegalArgumentException("templateId"));
        DashboardEntity entity = data.toEntity();
        entity.setTemplate(tplEntity);
        entity.setActive(false);
        return new DashboardDto(dashRepo.save(entity));
    }

    @GetMapping("/{id}")
    public DashboardDto get(@PathVariable("id") long id) throws NotFoundException {
        return new DashboardDto(dashRepo.findById(id)
                .orElseThrow(NotFoundException::new));
    }

    @PutMapping("/{id}")
    public DashboardDto update(
            @PathVariable("id") long id,
            @RequestBody DashboardDto data) throws NotFoundException {
        DashboardEntity entity = dashRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        boolean active = entity.isActive();
        TemplateEntity tplEntity = tplRepo.findById(data.getTemplateId())
                .orElseThrow(() -> new IllegalArgumentException("templateId"));
        data.toEntity(entity);
        entity.setTemplate(tplEntity);
        entity.setActive(active);
        return new DashboardDto(dashRepo.save(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable("id") long id) throws NotFoundException {
        DashboardEntity entity = dashRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        dashRepo.delete(entity);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/blocks")
    public List<BlockDto> listBlocks(@PathVariable("id") long id) throws NotFoundException {
        return dashRepo.findById(id)
                .orElseThrow(NotFoundException::new)
                .getBlocks()
                .stream()
                .map(BlockDto::new)
                .sorted(Comparator.comparingInt(BlockDto::getOrdinal))
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/blocks")
    public BlockDto addBlock(
            @PathVariable("id") long id,
            @RequestBody BlockDto data) throws NotFoundException {
        DashboardEntity parentEntity = dashRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        MonitorEntity monEntity = monitorRepo.findById(data.getMonitorId())
                .orElseThrow(() -> new IllegalArgumentException("monitorId"));

        BlockEntity entity = data.toEntity();
        entity.setMonitor(monEntity);
        entity.setDashboard(parentEntity);
        return new BlockDto(blockRepo.save(entity));
    }

    @PostMapping("/{id}/active")
    public ResponseEntity<Boolean> activateDashboard(@PathVariable("id") long id)
            throws NotFoundException {
        DashboardEntity entity = dashRepo.findById(id)
                .orElseThrow(NotFoundException::new);

        Set<MonitorEntity> activatedMon = new HashSet<>();
        try {
            for (BlockEntity blk : entity.getBlocks()) {
                if (!blk.isActive()) continue;
                try {
                    MonitorEntity mon = blk.getMonitor();
                    monitorService.activate(mon);
                    activatedMon.add(mon);
                } catch (TaskActivationException.TaskAlreadyActivated ignored) {
                }
            }
            entity.setActive(true);
            dashRepo.save(entity);
            return ResponseEntity.ok(true);
        } catch (TaskActivationException e) {
            for (MonitorEntity mon : activatedMon)
                monitorService.deactivate(mon);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @DeleteMapping("/{id}/active")
    public ResponseEntity<Boolean> deactivateDashboard(@PathVariable("id") long id)
            throws NotFoundException {
        DashboardEntity entity = dashRepo.findById(id)
                .orElseThrow(NotFoundException::new);

        Set<MonitorEntity> monitorsToStop = new HashSet<>(
                monitorRepo.listActiveMonitors());

        entity.setActive(false);
        dashRepo.save(entity);

        monitorsToStop.removeAll(
                monitorRepo.listActiveMonitors());

        for (MonitorEntity mon : monitorsToStop)
            monitorService.deactivate(mon);

        return ResponseEntity.ok(true);
    }

}

