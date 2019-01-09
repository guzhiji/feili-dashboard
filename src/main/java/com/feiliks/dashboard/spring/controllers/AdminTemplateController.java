package com.feiliks.dashboard.spring.controllers;

import com.feiliks.dashboard.spring.NotFoundException;
import com.feiliks.dashboard.spring.dto.TemplateDto;
import com.feiliks.dashboard.spring.entities.TemplateEntity;
import com.feiliks.dashboard.spring.repositories.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/templates")
public class AdminTemplateController {

    @Autowired
    private TemplateRepository tplRepo;

    @GetMapping
    public ModelAndView listTemplates() {
        Iterable<TemplateEntity> entities = tplRepo.findAll();
        Map<String, Object> data = new HashMap<>();
        data.put("list", entities);
        return new ModelAndView("admin/template/list", data);
    }

    @PostMapping
    public String createTemplate(TemplateDto formData) {
        tplRepo.save(formData.toEntity());
        return "redirect:/admin/templates";
    }

    @GetMapping("/new")
    public ModelAndView showTemplateEditor() {
        Map<String, Object> data = new HashMap<>();
        data.put("saveUrl", "/admin/templates");
        return new ModelAndView("admin/template/edit", data);
    }

    @GetMapping("/{id}")
    public ModelAndView showTemplateEditor(@PathVariable long id)
            throws NotFoundException {
        Optional<TemplateEntity> result = tplRepo.findById(id);
        TemplateEntity entity = result.orElseThrow(NotFoundException::new);

        Map<String, Object> data = new HashMap<>();
        data.put("saveUrl", "/admin/templates/" + id);
        data.put("entity", entity);

        return new ModelAndView("admin/template/edit", data);
    }

    @PostMapping("/{id}")
    public String modifyTemplate(
            @PathVariable long id,
            TemplateDto formData)
            throws NotFoundException {
        TemplateEntity entity = tplRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        formData.toEntity(entity);
        entity.setId(id);
        tplRepo.save(entity);
        return "redirect:/admin/templates";
    }

}
