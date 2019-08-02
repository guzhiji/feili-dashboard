package com.feiliks.dashboard.spring.repositories;

import com.feiliks.dashboard.spring.entities.BlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<BlockEntity, Long> {
}
