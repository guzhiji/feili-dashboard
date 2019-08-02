package com.feiliks.dashboard.spring.apis.controllers;

import com.feiliks.dashboard.spring.NotFoundException;
import com.feiliks.dashboard.spring.apis.dto.BlockDto;
import com.feiliks.dashboard.spring.apis.dto.FieldDto;
import com.feiliks.dashboard.spring.entities.BlockEntity;
import com.feiliks.dashboard.spring.entities.FieldEntity;
import com.feiliks.dashboard.spring.entities.MonitorEntity;
import com.feiliks.dashboard.spring.repositories.BlockRepo;
import com.feiliks.dashboard.spring.repositories.FieldRepo;
import com.feiliks.dashboard.spring.repositories.MonitorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/apis/blocks/{id}")
public class ApiBlockController {

    @Autowired
    private BlockRepo blockRepo;

    @Autowired
    private MonitorRepo monitorRepo;

    @Autowired
    private FieldRepo fieldRepo;

    @GetMapping
    public BlockDto get(@PathVariable("id") long id) throws NotFoundException {
        return new BlockDto(blockRepo.findById(id)
                .orElseThrow(NotFoundException::new));
    }

    @PutMapping
    public BlockDto update(
            @PathVariable("id") long id,
            @RequestBody BlockDto data) throws NotFoundException {
        BlockEntity entity = blockRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        MonitorEntity monEntity = monitorRepo.findById(data.getMonitorId())
                .orElseThrow(() -> new IllegalArgumentException("monitorId"));

        data.toEntity(entity);
        entity.setMonitor(monEntity);
        return new BlockDto(blockRepo.save(entity));
    }

    @DeleteMapping
    public ResponseEntity<?> remove(@PathVariable("id") long id) throws NotFoundException {
        BlockEntity entity = blockRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        blockRepo.delete(entity);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fields")
    public List<FieldDto> listFields(@PathVariable("id") long id) throws NotFoundException {
        return blockRepo.findById(id)
                .orElseThrow(NotFoundException::new)
                .getFields()
                .stream()
                .map(FieldDto::new)
                .sorted(Comparator.comparingInt(FieldDto::getOrdinal))
                .collect(Collectors.toList());
    }

    @PostMapping("/fields")
    public FieldDto addField(
            @PathVariable("id") long id,
            @RequestBody FieldDto data) throws NotFoundException {
        BlockEntity parentEntity = blockRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        FieldEntity entity = data.toEntity();
        entity.setBlock(parentEntity);
        return new FieldDto(fieldRepo.save(entity));
    }

}
