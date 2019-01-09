package com.feiliks.dashboard.spring.repositories;

import com.feiliks.dashboard.spring.entities.DataSourceEntity;
import org.springframework.data.repository.CrudRepository;

public interface DataSourceRepository extends CrudRepository<DataSourceEntity, Long> {
}
