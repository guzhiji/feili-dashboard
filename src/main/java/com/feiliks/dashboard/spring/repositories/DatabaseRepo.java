package com.feiliks.dashboard.spring.repositories;

import com.feiliks.dashboard.spring.entities.DatabaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DatabaseRepo extends JpaRepository<DatabaseEntity, Long> {
}
