package com.feiliks.dashboard.spring.repositories;

import com.feiliks.dashboard.spring.entities.DatabaseEntity;
import org.springframework.data.repository.CrudRepository;


public interface DatabaseRepository extends CrudRepository<DatabaseEntity, Long> {
}
