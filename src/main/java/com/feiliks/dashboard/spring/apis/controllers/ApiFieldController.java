package com.feiliks.dashboard.spring.apis.controllers;

import com.feiliks.dashboard.spring.NotFoundException;
import com.feiliks.dashboard.spring.apis.dto.FieldDto;
import com.feiliks.dashboard.spring.entities.FieldEntity;
import com.feiliks.dashboard.spring.repositories.FieldRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/apis/fields/{id}")
public class ApiFieldController {

    @Autowired
    private FieldRepo fieldRepo;

    @GetMapping
    public FieldDto get(@PathVariable("id") long id) throws NotFoundException {
        return new FieldDto(fieldRepo.findById(id)
                .orElseThrow(NotFoundException::new));
    }

    @PostMapping
    public FieldDto update(
            @PathVariable("id") long id,
            @RequestBody FieldDto data) throws NotFoundException {
        FieldEntity entity = fieldRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        data.toEntity(entity);
        return new FieldDto(fieldRepo.save(entity));
    }

    @DeleteMapping
    public ResponseEntity<?> remove(@PathVariable("id") long id) throws NotFoundException {
        FieldEntity entity = fieldRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        fieldRepo.delete(entity);
        return ResponseEntity.noContent().build();
    }

}
