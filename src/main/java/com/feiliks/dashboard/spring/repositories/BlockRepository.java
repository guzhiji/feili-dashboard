package com.feiliks.dashboard.spring.repositories;

import com.feiliks.dashboard.spring.entities.BlockEntity;
import org.springframework.data.repository.CrudRepository;

public interface BlockRepository extends CrudRepository<BlockEntity, Long> {
}
