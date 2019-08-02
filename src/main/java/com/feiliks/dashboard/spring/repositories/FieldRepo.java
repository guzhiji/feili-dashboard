package com.feiliks.dashboard.spring.repositories;

import com.feiliks.dashboard.spring.entities.FieldEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FieldRepo extends JpaRepository<FieldEntity, Long> {
}
