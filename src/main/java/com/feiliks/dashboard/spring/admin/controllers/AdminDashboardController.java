package com.feiliks.dashboard.spring.admin.controllers;

import com.feiliks.dashboard.spring.NotFoundException;
import com.feiliks.dashboard.spring.TaskActivationException;
import com.feiliks.dashboard.spring.admin.dto.BlockFormDto;
import com.feiliks.dashboard.spring.admin.dto.DashboardFormDto;
import com.feiliks.dashboard.spring.entities.BlockEntity;
import com.feiliks.dashboard.spring.entities.DashboardEntity;
import com.feiliks.dashboard.spring.entities.MonitorEntity;
import com.feiliks.dashboard.spring.entities.TemplateEntity;
import com.feiliks.dashboard.spring.repositories.BlockRepo;
import com.feiliks.dashboard.spring.repositories.DashboardRepo;
import com.feiliks.dashboard.spring.repositories.MonitorRepo;
import com.feiliks.dashboard.spring.repositories.TemplateRepo;
import com.feiliks.dashboard.spring.services.MonitorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.*;


@Controller
@RequestMapping("/admin/dashboards")
public class AdminDashboardController extends AbstractClassicController {

    @Autowired
    private DashboardRepo dashboardRepo;

    @Autowired
    private TemplateRepo tplRepo;

    @Autowired
    private MonitorRepo monitorRepo;

    @Autowired
    private BlockRepo blockRepo;

    @Autowired
    private MonitorService monitorService;

    @GetMapping
    public ModelAndView listDashboards(
            @RequestParam(value = "page", required = false)
                    Integer page) {
        if (page == null) page = 1;
        Page<DashboardEntity> entityPage = dashboardRepo.findAllByOrderByName(
                PageRequest.of(page - 1, 10));
        Map<String, Object> data = new HashMap<>();
        data.put("count", entityPage.getTotalElements());
        data.put("page_count", entityPage.getTotalPages());
        data.put("page", page);
        data.put("list", entityPage.getContent());
        return new ModelAndView("admin/dashboard/list", data);
    }

    @PostMapping
    public String createDashboard(
            @Valid DashboardFormDto formData,
            BindingResult vresult,
            RedirectAttributes ratts) {

        if (!checkValidation(vresult, ratts))
            return "redirect:/admin/dashboards/new";

        TemplateEntity template = tplRepo.findById(formData.getTemplateId())
                .orElse(null);
        if (template == null) {
            ratts.addFlashAttribute("flashMessage", "dashboard-tpl-empty");
            return "redirect:/admin/dashboards/new";
        }

        DashboardEntity entity = formData.toEntity();
        entity.setId(null);
        entity.setTemplate(template);
        dashboardRepo.save(entity);
        ratts.addFlashAttribute("flashMessage", "dashboard-saved");
        return "redirect:/admin/dashboards";

    }

    @GetMapping("/new")
    public ModelAndView showDashboardEditor() {
        Map<String, Object> data = new HashMap<>();
        data.put("mode", "create");
        data.put("saveUrl", "/admin/dashboards");
        data.put("templates", tplRepo.findAll());
        return new ModelAndView("admin/dashboard/edit", data);
    }

    @GetMapping("/{id}")
    public ModelAndView showDashboardEditor(@PathVariable long id)
            throws NotFoundException {
        Optional<DashboardEntity> result = dashboardRepo.findById(id);
        DashboardEntity entity = result.orElseThrow(NotFoundException::new);

        Map<String, Object> data = new HashMap<>();
        data.put("mode", "modify");
        data.put("saveUrl", "/admin/dashboards/" + id);
        data.put("entity", entity);
        data.put("templates", tplRepo.findAll());

        return new ModelAndView("admin/dashboard/edit", data);
    }

    @PostMapping("/{id}")
    public String modifyDashboard(
            @PathVariable long id,
            @Valid DashboardFormDto formData,
            BindingResult vresult,
            RedirectAttributes ratts)
            throws NotFoundException {

        if (!checkValidation(vresult, ratts))
            return "redirect:/admin/dashboards/" + id;

        DashboardEntity entity = dashboardRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        formData.toEntity(entity);
        entity.setId(id);

        TemplateEntity template = tplRepo.findById(formData.getTemplateId())
                .orElse(null);
        if (template == null) {
            ratts.addFlashAttribute("flashMessage", "dashboard-tpl-empty");
            return "redirect:/admin/dashboards/" + id;
        }
        entity.setTemplate(template);

        dashboardRepo.save(entity);
        ratts.addFlashAttribute("flashMessage", "dashboard-saved");
        return "redirect:/admin/dashboards";
    }

    @GetMapping("/{id}/blocks")
    public ModelAndView listDashboardBlocks(@PathVariable long id)
            throws NotFoundException {
        DashboardEntity entity = dashboardRepo.findById(id)
                .orElseThrow(NotFoundException::new);

        Map<String, Object> data = new HashMap<>();
        data.put("parent", entity);

        List<BlockEntity> blocks = new ArrayList<>(entity.getBlocks());
        blocks.sort(Comparator.comparingInt(BlockEntity::getOrdinal));
        data.put("list", blocks);

        return new ModelAndView("admin/block/list", data);
    }

    @PostMapping("/{id}/blocks")
    public String createBlock(
            @PathVariable long id,
            @Valid BlockFormDto formData,
            BindingResult vresult,
            RedirectAttributes ratts)
            throws NotFoundException {

        if (checkValidation(vresult, ratts)) {

            DashboardEntity parent = dashboardRepo.findById(id)
                    .orElseThrow(NotFoundException::new);
            MonitorEntity monitor = monitorRepo.findById(formData.getMonitorId())
                    .orElse(null);

            if (monitor == null) {
                ratts.addFlashAttribute("flashMessage", "monitor-not-found");
            } else {
                BlockEntity entity = formData.toEntity();
                entity.setId(null);
                entity.setDashboard(parent);
                entity.setMonitor(monitor);

                blockRepo.save(entity);
                ratts.addFlashAttribute("flashMessage", "block-saved");
            }

        }

        return "redirect:/admin/dashboards/" + id + "/blocks";
    }

    @GetMapping("/{id}/blocks/new")
    public ModelAndView showBlockEditor(@PathVariable long id)
            throws NotFoundException {
        DashboardEntity entity = dashboardRepo.findById(id)
                .orElseThrow(NotFoundException::new);

        Map<String, Object> data = new HashMap<>();
        data.put("mode", "create");
        data.put("parent", entity);
        data.put("saveUrl", "/admin/dashboards/" + id + "/blocks");

        data.put("monitors", monitorRepo.findAll());
        String[] dataRenderers = {
                "pie-chart", "category-chart", "time-chart", "data-table"
        };
        data.put("dataRenderers", dataRenderers);
        String[] resultHandlers = {
                "resultHandler1", "resultHandler2"
        };
        data.put("resultHandlers", resultHandlers);
        String[] msgHandlers = {
                "msgh1", "msgh2", "msgh3"
        };
        data.put("messageHandlers", msgHandlers);

        return new ModelAndView("admin/block/edit", data);
    }

    @PostMapping("/{id}/delete")
    public String deleteDashboard(
            @PathVariable long id,
            RedirectAttributes ratts)
            throws NotFoundException {
        deactivateDashboard(id, ratts);
        dashboardRepo.deleteById(id);
        ratts.addFlashAttribute("flashMessage", "dashboard-deleted");
        return "redirect:/admin/dashboards";
    }

    @PostMapping("/{id}/activate")
    public String activateDashboard(
            @PathVariable long id,
            RedirectAttributes ratts)
            throws NotFoundException {
        DashboardEntity entity = dashboardRepo.findById(id)
                .orElseThrow(NotFoundException::new);

        Set<MonitorEntity> activatedMon = new HashSet<>();
        try {
            for (BlockEntity blk : entity.getBlocks()) {
                if (!blk.isActive()) continue;
                try {
                    MonitorEntity mon = blk.getMonitor();
                    monitorService.activate(mon);
                    activatedMon.add(mon);
                } catch (TaskActivationException.TaskAlreadyActivated ignored) {
                }
            }
            entity.setActive(true);
            dashboardRepo.save(entity);
            ratts.addFlashAttribute("flashMessage", "dashboard-activated");
        } catch (TaskActivationException e) {
            e.printStackTrace();
            for (MonitorEntity mon : activatedMon)
                monitorService.deactivate(mon);
            ratts.addFlashAttribute("flashMessage", "dashboard-not-activated");
        }

        return "redirect:/admin/dashboards";
    }

    @PostMapping("/{id}/deactivate")
    public String deactivateDashboard(
            @PathVariable long id,
            RedirectAttributes ratts)
            throws NotFoundException {
        DashboardEntity entity = dashboardRepo.findById(id)
                .orElseThrow(NotFoundException::new);

        Set<MonitorEntity> monitorsToStop = new HashSet<>(
                monitorRepo.listActiveMonitors());

        entity.setActive(false);
        dashboardRepo.save(entity);

        monitorsToStop.removeAll(
                monitorRepo.listActiveMonitors());

        for (MonitorEntity mon : monitorsToStop)
            monitorService.deactivate(mon);

        ratts.addFlashAttribute("flashMessage", "dashboard-deactivated");
        return "redirect:/admin/dashboards";
    }

}
