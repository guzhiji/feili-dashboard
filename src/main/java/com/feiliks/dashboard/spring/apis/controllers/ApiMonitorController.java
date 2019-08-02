package com.feiliks.dashboard.spring.apis.controllers;

import com.feiliks.dashboard.TaskUtils;
import com.feiliks.dashboard.spring.NotFoundException;
import com.feiliks.dashboard.spring.apis.dto.MonitorDto;
import com.feiliks.dashboard.spring.entities.DatabaseEntity;
import com.feiliks.dashboard.spring.entities.MonitorEntity;
import com.feiliks.dashboard.spring.repositories.DatabaseRepo;
import com.feiliks.dashboard.spring.repositories.MonitorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/apis/monitors")
public class ApiMonitorController {

    @Autowired
    private MonitorRepo monitorRepo;

    @Autowired
    private DatabaseRepo dbRepo;

    @GetMapping
    public List<MonitorDto> list() {
        return monitorRepo.findAll().stream()
                .map(MonitorDto::new)
                .collect(Collectors.toList());
    }

    @PostMapping
    public MonitorDto create(@RequestBody MonitorDto data) {
        DatabaseEntity dbEntity = null;
        if (data.getDatabaseId() != null) {
            dbEntity = dbRepo.findById(data.getDatabaseId())
                    .orElseThrow(() -> new IllegalArgumentException("databaseId"));
        }
        if (!TaskUtils.validateMonitor(data.getJavaClass())) {
            throw new IllegalArgumentException("javaClass");
        }
        MonitorEntity monEntity = data.toEntity();
        monEntity.setDatabase(dbEntity);
        return new MonitorDto(monitorRepo.save(monEntity));
    }

    @GetMapping("/{id}")
    public MonitorDto get(@PathVariable("id") long id) throws NotFoundException {
        return new MonitorDto(monitorRepo.findById(id)
                .orElseThrow(NotFoundException::new));
    }

    @PutMapping("/{id}")
    public MonitorDto update(
            @PathVariable("id") long id,
            @RequestBody MonitorDto data) throws NotFoundException {
        MonitorEntity monEntity = monitorRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        DatabaseEntity dbEntity = null;
        if (data.getDatabaseId() != null) {
            dbEntity = dbRepo.findById(data.getDatabaseId())
                    .orElseThrow(() -> new IllegalArgumentException("databaseId"));
        }
        if (!TaskUtils.validateMonitor(data.getJavaClass())) {
            throw new IllegalArgumentException("javaClass");
        }
        data.toEntity(monEntity);
        monEntity.setDatabase(dbEntity);
        return new MonitorDto(monitorRepo.save(monEntity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable("id") long id) throws NotFoundException {
        MonitorEntity monEntity = monitorRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        monitorRepo.delete(monEntity);
        return ResponseEntity.noContent().build();
    }

}
