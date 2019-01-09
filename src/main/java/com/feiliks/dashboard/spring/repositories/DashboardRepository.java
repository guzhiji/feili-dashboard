package com.feiliks.dashboard.spring.repositories;

import com.feiliks.dashboard.spring.entities.DashboardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;


public interface DashboardRepository extends PagingAndSortingRepository<DashboardEntity, Long> {

    Optional<DashboardEntity> findByPathKey(String pathKey);

    Iterable<DashboardEntity> findByActiveOrderByName(boolean isActive);
    Page<DashboardEntity> findByActiveOrderByName(boolean isActive, Pageable pageable);
    Page<DashboardEntity> findAllByOrderByName(Pageable pageable);

}
