package com.feiliks.dashboard.spring.controllers;


import com.feiliks.dashboard.spring.NotFoundException;
import com.feiliks.dashboard.spring.dto.BlockFormDto;
import com.feiliks.dashboard.spring.entities.BlockEntity;
import com.feiliks.dashboard.spring.entities.DataSourceEntity;
import com.feiliks.dashboard.spring.entities.MessageNotifierEntity;
import com.feiliks.dashboard.spring.repositories.BlockRepository;
import com.feiliks.dashboard.spring.repositories.DataSourceRepository;
import com.feiliks.dashboard.spring.repositories.MessageNotifierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/admin/blocks")
public class AdminBlockController {

    @Autowired
    private BlockRepository blockRepo;

    @Autowired
    private DataSourceRepository dataSourceRepo;

    @Autowired
    private MessageNotifierRepository notifierRepo;

    @GetMapping("/{id}")
    public ModelAndView showBlockEditor(@PathVariable long id)
            throws NotFoundException {
        BlockEntity entity = blockRepo.findById(id)
                .orElseThrow(NotFoundException::new);

        Map<String, Object> data = new HashMap<>();
        data.put("entity", entity);
        data.put("parent", entity.getDashboard());
        data.put("mode", "modify");
        data.put("saveUrl", "/admin/blocks/" + id);
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

    @PostMapping("/{id}")
    public String modifyBlock(
            @PathVariable long id,
            BlockFormDto formData,
            RedirectAttributes ratts)
            throws NotFoundException {

        BlockEntity entity = blockRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        long dashboardId = entity.getDashboard().getId();

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
            return "redirect:/admin/blocks/" + id;
        }

        formData.toEntity(entity);
        entity.setId(id);
        entity.setDataSource(dataSourceEntity);
        entity.setMessageNotifier(notifierEntity);

        blockRepo.save(entity);
        ratts.addFlashAttribute("flashMessage", "block-saved");

        return "redirect:/admin/dashboards/" + dashboardId + "/blocks";
    }

    @PostMapping("/{id}/delete")
    public String deleteBlock(
            @PathVariable long id,
            RedirectAttributes ratts)
            throws NotFoundException {
        BlockEntity entity = blockRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        long dashboardId = entity.getDashboard().getId();
        blockRepo.deleteById(id);
        ratts.addFlashAttribute("flashMessage", "block-deleted");
        return "redirect:/admin/dashboards/" + dashboardId + "/blocks";
    }

}
