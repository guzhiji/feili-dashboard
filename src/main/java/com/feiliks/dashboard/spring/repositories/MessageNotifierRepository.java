package com.feiliks.dashboard.spring.repositories;

import com.feiliks.dashboard.spring.entities.MessageNotifierEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageNotifierRepository extends CrudRepository<MessageNotifierEntity, Long> {

    @Query("SELECT n FROM MessageNotifierEntity AS n JOIN n.blocks AS b JOIN b.dashboard AS bd WHERE n.isMonitor=false AND b.active=true AND bd.active=true")
    List<MessageNotifierEntity> listActiveNotifiersExceptMonitors();

    @Query("SELECT n FROM MessageNotifierEntity AS n JOIN n.blocks AS b JOIN b.dashboard AS bd WHERE b.active=true AND bd.active=true")
    List<MessageNotifierEntity> listActiveNotifiers();

}
