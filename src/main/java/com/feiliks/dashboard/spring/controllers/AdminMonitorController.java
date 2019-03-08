package com.feiliks.dashboard.spring.controllers;

import com.feiliks.dashboard.TaskUtils;
import com.feiliks.dashboard.spring.NotFoundException;
import com.feiliks.dashboard.spring.dto.MonitorFormDto;
import com.feiliks.dashboard.spring.entities.DatabaseEntity;
import com.feiliks.dashboard.spring.entities.MonitorEntity;
import com.feiliks.dashboard.spring.repositories.DatabaseRepository;
import com.feiliks.dashboard.spring.repositories.MonitorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/admin/monitors")
public class AdminMonitorController {

    @Autowired
    private MonitorRepository monitorRepo;

    @Autowired
    private DatabaseRepository dbRepo;

    @GetMapping
    public ModelAndView listMonitors() {
        Map<String, Object> data = new HashMap<>();
        data.put("list", monitorRepo.findAll());

        return new ModelAndView("admin/monitor/list", data);
    }

    @PostMapping
    public String createMonitor(
            @Valid MonitorFormDto formData,
            BindingResult vresult,
            RedirectAttributes ratts) {

        if (vresult.hasErrors()) {
            FieldError fe = vresult.getFieldError();
            if (fe != null)
                ratts.addFlashAttribute("flashMessage", fe.getDefaultMessage());
            return "redirect:/admin/monitors/new";
        }

        DatabaseEntity dbEntity = null;
        if (formData.getDatabaseId() != null) {
            dbEntity = dbRepo.findById(formData.getDatabaseId())
                    .orElse(null);
            if (dbEntity == null) {
                ratts.addFlashAttribute("flashMessage", "monitor-database-invalid");
                return "redirect:/admin/monitors/new";
            }
        }

        MonitorEntity entity = formData.toEntity();
        if (!TaskUtils.validateMonitor(entity.getJavaClass())) {
            ratts.addFlashAttribute("flashMessage", "monitor-javaclass-invalid");
            return "redirect:/admin/monitors/new";
        }
        entity.setDatabase(dbEntity);

        monitorRepo.save(entity);
        ratts.addFlashAttribute("flashMessage", "monitor-saved");

        return "redirect:/admin/monitors";
    }

    @GetMapping("/new")
    public ModelAndView showMonitorEditor() {
        Map<String, Object> data = new HashMap<>();
        data.put("mode", "create");
        data.put("saveUrl", "/admin/monitors");
        data.put("databases", dbRepo.findAll());
        return new ModelAndView("admin/monitor/edit", data);
    }

    @GetMapping("/{id}")
    public ModelAndView showMonitorEditor(@PathVariable long id)
            throws NotFoundException {
        MonitorEntity entity = monitorRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        Map<String, Object> data = new HashMap<>();
        data.put("mode", "modify");
        data.put("saveUrl", "/admin/monitors/" + id);
        data.put("entity", new MonitorFormDto(entity));
        data.put("databases", dbRepo.findAll());
        return new ModelAndView("admin/monitor/edit", data);
    }

    @PostMapping("/{id}")
    public String modifyMonitor(
            @PathVariable long id,
            @Valid MonitorFormDto formData,
            BindingResult vresult,
            RedirectAttributes ratts)
            throws NotFoundException {

        if (vresult.hasErrors()) {
            FieldError fe = vresult.getFieldError();
            if (fe != null)
                ratts.addFlashAttribute("flashMessage", fe.getDefaultMessage());
            return "redirect:/admin/monitors/" + id;
        }

        DatabaseEntity dbEntity = null;
        if (formData.getDatabaseId() != null) {
            dbEntity = dbRepo.findById(formData.getDatabaseId())
                    .orElse(null);
            if (dbEntity == null) {
                ratts.addFlashAttribute("flashMessage", "monitor-database-invalid");
                return "redirect:/admin/monitors/" + id;
            }
        }

        MonitorEntity entity = monitorRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        formData.toEntity(entity);
        entity.setId(id);
        entity.setDatabase(dbEntity);

        if (!TaskUtils.validateMonitor(entity.getJavaClass())) {
            ratts.addFlashAttribute("flashMessage", "monitor-javaclass-invalid");
            return "redirect:/admin/monitors/" + id;
        }

        monitorRepo.save(entity);
        ratts.addFlashAttribute("flashMessage", "monitor-saved");

        return "redirect:/admin/monitors";
    }

    @PostMapping("/{id}/delete")
    public String deleteMonitor(
            @PathVariable long id,
            RedirectAttributes ratts) {

        monitorRepo.deleteById(id);
        ratts.addFlashAttribute("flashMessage", "monitor-deleted");

        return "redirect:/admin/monitors";
    }

}
