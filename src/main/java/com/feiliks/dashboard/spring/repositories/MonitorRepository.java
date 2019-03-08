package com.feiliks.dashboard.spring.repositories;

import com.feiliks.dashboard.spring.entities.MonitorEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface MonitorRepository extends CrudRepository<MonitorEntity, Long> {

    @Query("SELECT m FROM MonitorEntity AS m JOIN m.blocks AS b JOIN b.dashboard AS bd WHERE b.active=true AND bd.active=true")
    List<MonitorEntity> listActiveMonitors();

}
