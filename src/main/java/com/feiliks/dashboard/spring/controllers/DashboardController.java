package com.feiliks.dashboard.spring.controllers;

import com.feiliks.dashboard.IMonitor;
import com.feiliks.dashboard.spring.NotFoundException;
import com.feiliks.dashboard.spring.dto.DashboardDto;
import com.feiliks.dashboard.spring.entities.DashboardEntity;
import com.feiliks.dashboard.spring.entities.DataSourceEntity;
import com.feiliks.dashboard.spring.repositories.DashboardRepository;
import com.feiliks.dashboard.spring.repositories.DataSourceRepository;
import com.feiliks.dashboard.spring.services.DashboardTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardRepository dashboardRepo;

    @Autowired
    private DataSourceRepository dataSourceRepo;

    @Autowired
    private DashboardTaskService dashboardTaskService;

    @GetMapping("/{pathKey}")
    public ModelAndView show(@PathVariable String pathKey)
            throws NotFoundException {
        Optional<DashboardEntity> result = dashboardRepo.findByPathKey(pathKey);
        DashboardEntity entity = result.orElseThrow(NotFoundException::new);
        Map<String, Object> data = new HashMap<>();
        data.put("id", entity.getId());
        data.put("dashboard", entity);
        return new ModelAndView(
                entity.getTemplate().getInternalName(),
                data);
    }

    @GetMapping("/{id}.json")
    public ResponseEntity<DashboardDto> getDashboardData(
            @PathVariable Long id)
            throws NotFoundException {
        DashboardEntity entity = dashboardRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        return ResponseEntity.ok(new DashboardDto(entity));
    }

    @GetMapping("/datasource/{id}.json")
    private void getData(
            @PathVariable Long id,
            HttpServletResponse resp)
            throws NotFoundException, IOException {
        DataSourceEntity dataSource = dataSourceRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        IMonitor monitor = dashboardTaskService.getMonitorTask(
                dataSource.getMonitor());
        resp.addHeader("Content-Type", "application/json");
        if (monitor != null) {
            resp.getWriter().write(
                    monitor.retrieveDataSource(
                            dataSource.getInternalName()));
        } else {
            resp.getWriter().write("null");
        }
    }

}
