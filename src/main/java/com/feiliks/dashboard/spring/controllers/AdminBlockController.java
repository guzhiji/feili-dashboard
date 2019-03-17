package com.feiliks.dashboard.spring.controllers;

import com.feiliks.dashboard.spring.NotFoundException;
import com.feiliks.dashboard.spring.dto.BlockFormDto;
import com.feiliks.dashboard.spring.dto.FieldDto;
import com.feiliks.dashboard.spring.entities.BlockEntity;
import com.feiliks.dashboard.spring.entities.FieldEntity;
import com.feiliks.dashboard.spring.repositories.BlockRepository;
import com.feiliks.dashboard.spring.repositories.FieldRepository;
import com.feiliks.dashboard.spring.repositories.MonitorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.*;


@Controller
@RequestMapping("/admin/blocks")
public class AdminBlockController extends AbstractClassicController {

    @Autowired
    private BlockRepository blockRepo;

    @Autowired
    private MonitorRepository monitorRepo;

    @Autowired
    private FieldRepository fieldRepo;

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

    @PostMapping("/{id}")
    public String modifyBlock(
            @PathVariable long id,
            @Valid BlockFormDto formData,
            BindingResult vresult,
            RedirectAttributes ratts)
            throws NotFoundException {

        BlockEntity entity = blockRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        long dashboardId = entity.getDashboard().getId();

        if (checkValidation(vresult, ratts)) {

            formData.toEntity(entity);
            entity.setId(id);

            blockRepo.save(entity);
            ratts.addFlashAttribute("flashMessage", "block-saved");
        }

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

    @GetMapping("/{id}/fields")
    public ModelAndView listFields(@PathVariable long id)
            throws NotFoundException {
        BlockEntity entity = blockRepo.findById(id)
                .orElseThrow(NotFoundException::new);

        Map<String, Object> data = new HashMap<>();
        data.put("parent", entity);
        List<FieldEntity> fields = new ArrayList<>(entity.getFields());
        fields.sort(Comparator.comparingInt(FieldEntity::getOrdinal));
        data.put("list", fields);

        return new ModelAndView("admin/field/list", data);
    }

    @PostMapping("/{id}/fields")
    public String createField(
            @PathVariable long id,
            @Valid FieldDto formData,
            BindingResult vresult,
            RedirectAttributes ratts)
            throws NotFoundException {
        BlockEntity parent = blockRepo.findById(id)
                .orElseThrow(NotFoundException::new);

        if (checkValidation(vresult, ratts)) {
            FieldEntity entity = formData.toEntity();
            entity.setId(null);
            entity.setBlock(parent);
            fieldRepo.save(entity);

            ratts.addFlashAttribute("flashMessage", "field-saved");
        }

        return "redirect:/admin/blocks/" + id + "/fields";
    }

    @GetMapping("/{id}/fields/new")
    public ModelAndView showFieldEditor(@PathVariable long id)
            throws NotFoundException {
        BlockEntity entity = blockRepo.findById(id)
                .orElseThrow(NotFoundException::new);

        Map<String, Object> data = new HashMap<>();
        data.put("mode", "create");
        data.put("parent", entity);
        data.put("saveUrl", "/admin/blocks/" + id + "/fields");

        return new ModelAndView("admin/field/edit", data);
    }

}
