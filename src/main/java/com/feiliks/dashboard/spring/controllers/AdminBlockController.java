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
        data.put("saveUrl", "/admin/blocks/" + id);
        String[] dataRenderers = {
                "pie", "line", "bar"
        };
        data.put("dataRenderers", dataRenderers);

        return new ModelAndView("admin/block/edit", data);
    }

    @PostMapping("/{id}")
    public String modifyBlock(
            @PathVariable long id,
            BlockFormDto data)
            throws NotFoundException {

        BlockEntity entity = blockRepo.findById(id)
                .orElseThrow(NotFoundException::new);

        DataSourceEntity dataSourceEntity = dataSourceRepo.findById(
                data.getDataSourceId()).orElse(null);
        MessageNotifierEntity notifierEntity = notifierRepo.findById(
                data.getMessageNotifierId()).orElse(null);

        data.toEntity(entity);
        entity.setId(id);
        entity.setDataSource(dataSourceEntity);
        entity.setMessageNotifier(notifierEntity);

        blockRepo.save(entity);

        return "redirect:/admin/dashboards/" + entity.getDashboard().getId() + "/blocks";
    }

}
