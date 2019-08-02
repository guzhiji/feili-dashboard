package com.feiliks.dashboard.spring.admin.controllers;


import com.feiliks.dashboard.spring.NotFoundException;
import com.feiliks.dashboard.spring.admin.dto.FieldDto;
import com.feiliks.dashboard.spring.entities.FieldEntity;
import com.feiliks.dashboard.spring.repositories.FieldRepository;
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
@RequestMapping("/admin/fields")
public class AdminFieldController extends AbstractClassicController {

    @Autowired
    private FieldRepository fieldRepo;

    @GetMapping("/{id}")
    public ModelAndView showFieldEditor(@PathVariable long id)
            throws NotFoundException {
        FieldEntity entity = fieldRepo.findById(id)
                .orElseThrow(NotFoundException::new);

        Map<String, Object> data = new HashMap<>();
        data.put("entity", entity);
        data.put("parent", entity.getBlock());
        data.put("mode", "modify");
        data.put("saveUrl", "/admin/fields/" + id);

        return new ModelAndView("admin/field/edit", data);
    }

    @PostMapping("/{id}")
    public String modifyField(
            @PathVariable long id,
            @Valid FieldDto formData,
            BindingResult vresult,
            RedirectAttributes ratts)
            throws NotFoundException {

        FieldEntity entity = fieldRepo.findById(id)
                .orElseThrow(NotFoundException::new);

        if (checkValidation(vresult, ratts)) {

            formData.toEntity(entity);
            entity.setId(id);

            fieldRepo.save(entity);
            ratts.addFlashAttribute("flashMessage", "field-saved");
        }

        return "redirect:/admin/blocks/" + entity.getBlock().getId() + "/fields";
    }

    @PostMapping("/{id}/delete")
    public String deleteField(
            @PathVariable long id,
            RedirectAttributes ratts)
            throws NotFoundException {

        FieldEntity entity = fieldRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        long blockId = entity.getBlock().getId();
        fieldRepo.deleteById(id);
        ratts.addFlashAttribute("flashMessage", "field-deleted");
        return "redirect:/admin/blocks/" + blockId + "/fields";
    }

}
