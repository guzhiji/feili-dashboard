package com.feiliks.dashboard.spring.controllers;

import com.feiliks.dashboard.spring.NotFoundException;
import com.feiliks.dashboard.spring.TaskActivationException;
import com.feiliks.dashboard.spring.dto.BlockFormDto;
import com.feiliks.dashboard.spring.dto.DashboardFormDto;
import com.feiliks.dashboard.spring.entities.*;
import com.feiliks.dashboard.spring.repositories.*;
import com.feiliks.dashboard.spring.services.DashboardTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;


@Controller
@RequestMapping("/admin/dashboards")
public class AdminDashboardController {

    @Autowired
    private DashboardRepository dashboardRepo;

    @Autowired
    private TemplateRepository tplRepo;

    @Autowired
    private MonitorRepository monitorRepo;

    @Autowired
    private DataSourceRepository dataSourceRepo;

    @Autowired
    private MessageNotifierRepository notifierRepo;

    @Autowired
    private DashboardTaskService dashboardTaskService;

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
    public String createDashboard(DashboardFormDto formData) {
        TemplateEntity template = tplRepo.findById(formData.getTemplateId())
                .orElse(null);
        if (template != null) {

            DashboardEntity entity = formData.toEntity();
            entity.setId(null);
            entity.setTemplate(template);
            dashboardRepo.save(entity);

        }
        return "redirect:/admin/dashboards";
    }

    @GetMapping("/new")
    public ModelAndView showDashboardEditor() {
        Map<String, Object> data = new HashMap<>();
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
        data.put("saveUrl", "/admin/dashboards/" + id);
        data.put("entity", entity);
        data.put("templates", tplRepo.findAll());

        return new ModelAndView("admin/dashboard/edit", data);
    }

    @PostMapping("/{id}")
    public String modifyDashboard(
            @PathVariable long id,
            DashboardFormDto formData,
            RedirectAttributes ratts)
            throws NotFoundException {
        DashboardEntity entity = dashboardRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        formData.toEntity(entity);
        entity.setId(id);

        TemplateEntity template = tplRepo.findById(formData.getTemplateId())
                .orElse(null);
        if (template != null) {

            entity.setTemplate(template);
            dashboardRepo.save(entity);
            ratts.addFlashAttribute("success", "dashboard " + id + " saved");
        }
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
            BlockFormDto data)
            throws NotFoundException {
        DashboardEntity parent = dashboardRepo.findById(id)
                .orElseThrow(NotFoundException::new);

        DataSourceEntity dataSourceEntity = dataSourceRepo.findById(
                data.getDataSourceId()).orElse(null);
        MessageNotifierEntity notifierEntity = notifierRepo.findById(
                data.getMessageNotifierId()).orElse(null);

        BlockEntity entity = data.toEntity();
        entity.setDashboard(parent);
        entity.setDataSource(dataSourceEntity);
        entity.setMessageNotifier(notifierEntity);

        parent.getBlocks().add(entity);
        dashboardRepo.save(parent);

        return "redirect:/admin/dashboards/" + id + "/blocks";
    }

    @GetMapping("/{id}/blocks/new")
    public ModelAndView showBlockEditor(@PathVariable long id)
            throws NotFoundException {
        DashboardEntity entity = dashboardRepo.findById(id)
                .orElseThrow(NotFoundException::new);

        Map<String, Object> data = new HashMap<>();
        data.put("parent", entity);
        data.put("saveUrl", "/admin/dashboards/" + id + "/blocks");

        String[] dataRenderers = {
                "pie", "line", "bar"
        };
        data.put("dataRenderers", dataRenderers);

        return new ModelAndView("admin/block/edit", data);
    }

    @PostMapping("/{id}/delete")
    public String deleteDashboard(@PathVariable long id)
            throws NotFoundException {
        deactivateDashboard(id);
        dashboardRepo.deleteById(id);
        return "redirect:/admin/dashboards";
    }

    @PostMapping("/{id}/activate")
    public String activateDashboard(@PathVariable long id)
            throws NotFoundException {
        DashboardEntity entity = dashboardRepo.findById(id)
                .orElseThrow(NotFoundException::new);

        Set<String> activated = new HashSet<>();
        try {
            for (BlockEntity blk : entity.getBlocks()) {
                DataSourceEntity src = blk.getDataSource();
                MonitorEntity mon = null;
                if (src != null) mon = src.getMonitor();
                MessageNotifierEntity ntf = blk.getMessageNotifier();
                if (ntf == null) {
                    if (mon != null) {
                        if (dashboardTaskService.activate(mon))
                            activated.add(mon.getJavaClass());
                    }
                } else if (mon == null) {
                    if (dashboardTaskService.activate(ntf))
                        activated.add(ntf.getJavaClass());
                } else {
                    if (dashboardTaskService.activate(mon, ntf))
                        activated.add(mon.getJavaClass());
                }
            }
            entity.setActive(true);
            dashboardRepo.save(entity);
        } catch (TaskActivationException e) {
            for (String javaClass : activated)
                dashboardTaskService.deactivate(javaClass);
        }

        return "redirect:/admin/dashboards/" + id;
    }

    @PostMapping("/{id}/deactivate")
    public String deactivateDashboard(@PathVariable long id)
            throws NotFoundException {
        DashboardEntity entity = dashboardRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        Set<String> tasksToStop = new HashSet<>();

        List<MonitorEntity> monPre = monitorRepo.listActiveMonitors();
        List<MessageNotifierEntity> ntfPre = notifierRepo.listActiveNotifiersExceptMonitors();
        for (MonitorEntity mon : monPre)
            tasksToStop.add(mon.getJavaClass());
        for (MessageNotifierEntity ntf : ntfPre)
            tasksToStop.add(ntf.getJavaClass());

        entity.setActive(false);
        dashboardRepo.save(entity);

        List<MonitorEntity> monPost = monitorRepo.listActiveMonitors();
        List<MessageNotifierEntity> ntfPost = notifierRepo.listActiveNotifiersExceptMonitors();
        for (MonitorEntity mon : monPost)
            tasksToStop.remove(mon.getJavaClass());
        for (MessageNotifierEntity ntf : ntfPost)
            tasksToStop.remove(ntf.getJavaClass());

        for (String taskJavaClass : tasksToStop)
            dashboardTaskService.deactivate(taskJavaClass);

        return "redirect:/admin/dashboards/" + id;
    }

}
