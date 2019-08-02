package com.feiliks.dashboard.spring.repositories;

import com.feiliks.dashboard.spring.entities.BlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepo extends JpaRepository<BlockEntity, Long> {
}
