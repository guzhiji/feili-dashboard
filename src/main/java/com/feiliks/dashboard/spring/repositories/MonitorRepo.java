package com.feiliks.dashboard.spring.repositories;

import com.feiliks.dashboard.spring.entities.MonitorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;


public interface MonitorRepo extends JpaRepository<MonitorEntity, Long> {

    @Query("SELECT m FROM MonitorEntity AS m JOIN m.blocks AS b JOIN b.dashboard AS bd WHERE b.active=true AND bd.active=true")
    Set<MonitorEntity> listActiveMonitors();

}
