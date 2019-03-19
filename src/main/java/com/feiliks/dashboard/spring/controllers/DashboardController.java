package com.feiliks.dashboard.spring.controllers;

import com.feiliks.dashboard.AbstractMonitor;
import com.feiliks.dashboard.spring.NotFoundException;
import com.feiliks.dashboard.spring.dto.DashboardDto;
import com.feiliks.dashboard.spring.dto.MonitorDto;
import com.feiliks.dashboard.spring.entities.BlockEntity;
import com.feiliks.dashboard.spring.entities.DashboardEntity;
import com.feiliks.dashboard.spring.repositories.BlockRepository;
import com.feiliks.dashboard.spring.repositories.DashboardRepository;
import com.feiliks.dashboard.spring.services.MonitorService;

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


@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardRepository dashboardRepo;

    @Autowired
    private BlockRepository blockRepo;

    @Autowired
    private MonitorService monitorService;

    /**
     * get dashboard structural information.
     *
     * @param id    dashboard id
     * @return dashboard structure
     * @throws NotFoundException
     */
    @GetMapping("/{id}.json")
    public ResponseEntity<DashboardDto> getDashboardData(
            @PathVariable Long id)
            throws NotFoundException {
        DashboardEntity entity = dashboardRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        if (!entity.isActive())
            throw new NotFoundException();
        DashboardDto dto = new DashboardDto(entity);
        for (MonitorDto md : dto.getMonitors()) {
            AbstractMonitor monitor = monitorService.getMonitor(md.getId());
            if (monitor != null) {
                md.setResultSources(monitor.getResultSources());
                md.setMessageSources(monitor.getMessageSources());
            }
        }
        return ResponseEntity.ok(dto);
    }

    /**
     * get result data.
     *
     * @param id    block id
     * @param rs    result source
     * @param resp  HttpServletResponse
     * @throws NotFoundException
     * @throws IOException
     */
    @GetMapping("/block/{id}/result/{rs}.json")
    public void getData(
            @PathVariable Long id,
            @PathVariable String rs,
            HttpServletResponse resp)
            throws NotFoundException, IOException {

        BlockEntity blk = blockRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        if (!blk.isActive())
            throw new NotFoundException();
        AbstractMonitor monitor = monitorService.getMonitor(blk.getMonitor());
        if (monitor == null)
            throw new NotFoundException();
        resp.setHeader("Content-Type", "application/json");
        resp.getWriter().write(
                monitor.retrieveResult(rs));

    }

    @GetMapping("/monitor/{id}/result/{rs}.json")
    public void getDataFromMonitor(
            @PathVariable Long id,
            @PathVariable String rs,
            HttpServletResponse resp)
            throws NotFoundException, IOException {

        AbstractMonitor monitor = monitorService.getMonitor(id);
        if (monitor == null)
            throw new NotFoundException();

        resp.setHeader("Content-Type", "application/json");
        resp.getWriter().write(
                monitor.retrieveResult(rs));

    }

    @GetMapping("/monitor/{id}/result-sources.json")
    public ResponseEntity<Map<String, String>> getResultSources(@PathVariable Long id)
            throws NotFoundException {
        AbstractMonitor monitor = monitorService.getMonitor(id);
        if (monitor == null)
            throw new NotFoundException();

        return ResponseEntity.ok(monitor.getResultSources());
    }

    @GetMapping("/monitor/{id}/message-sources.json")
    public ResponseEntity<Map<String, String>> getMessageSources(@PathVariable Long id)
            throws NotFoundException {
        AbstractMonitor monitor = monitorService.getMonitor(id);
        if (monitor == null)
            throw new NotFoundException();

        return ResponseEntity.ok(monitor.getMessageSources());
    }

    @GetMapping("/{pathKey}")
    public ModelAndView show(@PathVariable String pathKey)
            throws NotFoundException {
        DashboardEntity entity = dashboardRepo.findByPathKey(pathKey)
                .orElseThrow(NotFoundException::new);
        if (!entity.isActive())
            throw new NotFoundException();
        Map<String, Object> data = new HashMap<>();
        data.put("dashboard", entity);
        return new ModelAndView("dashboard/" +
                entity.getTemplate().getInternalName(),
                data);
    }

}
