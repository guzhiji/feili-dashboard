package com.feiliks.dashboard.spring.repositories;

import com.feiliks.dashboard.spring.entities.DashboardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface DashboardRepo extends JpaRepository<DashboardEntity, Long> {

    Optional<DashboardEntity> findByPathKey(String pathKey);

    List<DashboardEntity> findByActiveOrderByName(boolean isActive);
    Page<DashboardEntity> findByActiveOrderByName(boolean isActive, Pageable pageable);
    List<DashboardEntity> findAllByOrderByName();
    Page<DashboardEntity> findAllByOrderByName(Pageable pageable);

}
