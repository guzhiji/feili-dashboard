package com.feiliks.dashboard.spring.admin.controllers;

import com.feiliks.dashboard.spring.NotFoundException;
import com.feiliks.dashboard.spring.admin.dto.TemplateDto;
import com.feiliks.dashboard.spring.entities.TemplateEntity;
import com.feiliks.dashboard.spring.repositories.TemplateRepository;

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
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/admin/templates")
public class AdminTemplateController extends AbstractClassicController {

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
    public String createTemplate(
        @Valid TemplateDto formData,
        BindingResult vresult,
        RedirectAttributes ratts) {
        if (!checkValidation(vresult, ratts))
            return "redirect:/admin/templates/new";
        tplRepo.save(formData.toEntity());
        ratts.addFlashAttribute("flashMessage", "template-saved");
        return "redirect:/admin/templates";
    }

    @GetMapping("/new")
    public ModelAndView showTemplateEditor() {
        Map<String, Object> data = new HashMap<>();
        data.put("mode", "create");
        data.put("saveUrl", "/admin/templates");
        return new ModelAndView("admin/template/edit", data);
    }

    @GetMapping("/{id}")
    public ModelAndView showTemplateEditor(@PathVariable long id)
            throws NotFoundException {

        TemplateEntity entity = tplRepo.findById(id)
                .orElseThrow(NotFoundException::new);

        Map<String, Object> data = new HashMap<>();
        data.put("mode", "modify");
        data.put("saveUrl", "/admin/templates/" + id);
        data.put("entity", entity);

        return new ModelAndView("admin/template/edit", data);
    }

    @PostMapping("/{id}")
    public String modifyTemplate(
            @PathVariable long id,
            @Valid TemplateDto formData,
            BindingResult vresult,
            RedirectAttributes ratts)
            throws NotFoundException {
        if (!checkValidation(vresult, ratts))
            return "redirect:/admin/templates/" + id;
        TemplateEntity entity = tplRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        formData.toEntity(entity);
        entity.setId(id);
        tplRepo.save(entity);
        ratts.addFlashAttribute("flashMessage", "template-saved");
        return "redirect:/admin/templates";
    }

    @PostMapping("/{id}/delete")
    public String deleteTemplate(
            @PathVariable long id,
            RedirectAttributes ratts) {
        tplRepo.deleteById(id);
        ratts.addFlashAttribute("flashMessage", "template-deleted");
        return "redirect:/admin/templates";
    }

}
