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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
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
    private BlockRepository blockRepo;

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
    public String createDashboard(
            @Valid DashboardFormDto formData,
            BindingResult vresult,
            RedirectAttributes ratts) {

        if (vresult.hasErrors()) {
            FieldError fe = vresult.getFieldError();
            if (fe != null)
                ratts.addFlashAttribute("flashMessage", fe.getDefaultMessage());
            return "redirect:/admin/dashboards/new";
        }

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

        if (vresult.hasErrors()) {
            FieldError fe = vresult.getFieldError();
            if (fe != null)
                ratts.addFlashAttribute("flashMessage", fe.getDefaultMessage());
            return "redirect:/admin/dashboards/" + id;
        }

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
            BlockFormDto formData,
            RedirectAttributes ratts)
            throws NotFoundException {
        DashboardEntity parent = dashboardRepo.findById(id)
                .orElseThrow(NotFoundException::new);

        DataSourceEntity dataSourceEntity = null;
        MessageNotifierEntity notifierEntity = null;
        if (formData.getDataSourceId() != null)
            dataSourceEntity = dataSourceRepo.findById(
                    formData.getDataSourceId()).orElse(null);
        if (formData.getMessageNotifierId() != null)
            notifierEntity = notifierRepo.findById(
                    formData.getMessageNotifierId()).orElse(null);
        if (dataSourceEntity == null && notifierEntity == null) {
            ratts.addFlashAttribute("flashMessage", "block-no-data");
            return "redirect:/admin/dashboards/" + id + "/blocks/new";
        }

        BlockEntity entity = formData.toEntity();
        entity.setId(null);
        entity.setDashboard(parent);
        entity.setDataSource(dataSourceEntity);
        entity.setMessageNotifier(notifierEntity);

        blockRepo.save(entity);
        ratts.addFlashAttribute("flashMessage", "block-saved");

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

        String[] dataRenderers = {
                "pie", "line", "bar"
        };
        data.put("dataRenderers", dataRenderers);
        String[] dataPreprocessors = {
                "preproc1", "preproc2"
        };
        data.put("dataPreprocessors", dataPreprocessors);
        String[] msgHandlers = {
                "msgh1", "msgh2", "msgh3"
        };
        data.put("messageHandlers", msgHandlers);

        data.put("dataSources", dataSourceRepo.findAll());
        data.put("messageNotifiers", notifierRepo.findAll());

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
        Set<MessageNotifierEntity> activatedNtf = new HashSet<>();
        try {
            for (BlockEntity blk : entity.getBlocks()) {
                DataSourceEntity src = blk.getDataSource();
                MonitorEntity mon = null;
                if (src != null) mon = src.getMonitor();
                MessageNotifierEntity ntf = blk.getMessageNotifier();
                if (ntf == null) {
                    if (mon != null) {
                        if (dashboardTaskService.activate(mon))
                            activatedMon.add(mon);
                    }
                } else if (mon == null) {
                    if (dashboardTaskService.activate(ntf))
                        activatedNtf.add(ntf);
                } else {
                    if (dashboardTaskService.activate(mon, ntf))
                        activatedMon.add(mon);
                }
            }
            entity.setActive(true);
            dashboardRepo.save(entity);
            ratts.addFlashAttribute("flashMessage", "dashboard-activated");
        } catch (TaskActivationException e) {
            for (MonitorEntity mon : activatedMon)
                dashboardTaskService.deactivate(mon);
            for (MessageNotifierEntity ntf : activatedNtf)
                dashboardTaskService.deactivate(ntf);
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

        List<MonitorEntity> monPre = monitorRepo.listActiveMonitors();
        List<MessageNotifierEntity> ntfPre = notifierRepo.listActiveNotifiersExceptMonitors();
        Set<MonitorEntity> monitorsToStop = new HashSet<>(monPre);
        Set<MessageNotifierEntity> notifiersToStop = new HashSet<>(ntfPre);

        entity.setActive(false);
        dashboardRepo.save(entity);

        List<MonitorEntity> monPost = monitorRepo.listActiveMonitors();
        List<MessageNotifierEntity> ntfPost = notifierRepo.listActiveNotifiersExceptMonitors();
        for (MonitorEntity mon : monPost)
            monitorsToStop.remove(mon);
        for (MessageNotifierEntity ntf : ntfPost)
            notifiersToStop.remove(ntf);

        for (MonitorEntity mon : monitorsToStop)
            dashboardTaskService.deactivate(mon);
        for (MessageNotifierEntity ntf : notifiersToStop)
            dashboardTaskService.deactivate(ntf);

        ratts.addFlashAttribute("flashMessage", "dashboard-deactivated");
        return "redirect:/admin/dashboards";
    }

}
