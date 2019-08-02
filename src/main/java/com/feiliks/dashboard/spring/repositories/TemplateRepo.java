package com.feiliks.dashboard.spring.repositories;

import com.feiliks.dashboard.spring.entities.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TemplateRepo extends JpaRepository<TemplateEntity, Long> {
}
