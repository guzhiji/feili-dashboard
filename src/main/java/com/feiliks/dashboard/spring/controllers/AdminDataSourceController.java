package com.feiliks.dashboard.spring.controllers;

import com.feiliks.dashboard.spring.NotFoundException;
import com.feiliks.dashboard.spring.dto.DataSourceFormDto;
import com.feiliks.dashboard.spring.entities.DataSourceEntity;
import com.feiliks.dashboard.spring.repositories.DataSourceRepository;
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
@RequestMapping("/admin/data-sources")
public class AdminDataSourceController {

    @Autowired
    private DataSourceRepository dataSourceRepo;

    @GetMapping("/{id}")
    public ModelAndView showDataSourceEditor(@PathVariable long id)
            throws NotFoundException {
        DataSourceEntity entity = dataSourceRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        Map<String, Object> data = new HashMap<>();
        data.put("mode", "modify");
        data.put("saveUrl", "/admin/data-sources/" + id);
        data.put("entity", entity);
        data.put("parent", entity.getMonitor());
        return new ModelAndView("admin/data-source/edit", data);
    }

    @PostMapping("/{id}")
    public String modifyDataSource(
            @PathVariable long id,
            @Valid DataSourceFormDto dataForm,
            BindingResult vresult,
            RedirectAttributes ratts)
            throws NotFoundException {

        if (vresult.hasErrors()) {
            FieldError fe = vresult.getFieldError();
            if (fe != null)
                ratts.addFlashAttribute("flashMessage", fe.getDefaultMessage());
            return "redirect:/admin/data-sources/" + id;
        }

        DataSourceEntity entity = dataSourceRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        long monitorId = entity.getMonitor().getId();

        dataForm.toEntity(entity);
        entity.setId(id);
        dataSourceRepo.save(entity);
        ratts.addFlashAttribute("flashMessage", "data-source-saved");

        return "redirect:/admin/monitors/" + monitorId + "/data-sources";
    }

    @PostMapping("/{id}/delete")
    public String deleteDataSource(
            @PathVariable long id,
            RedirectAttributes ratts)
            throws NotFoundException {

        DataSourceEntity entity = dataSourceRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        long monitorId = entity.getMonitor().getId();
        dataSourceRepo.delete(entity);
        ratts.addFlashAttribute("flashMessage", "data-source-deleted");

        return "redirect:/admin/monitors/" + monitorId + "/data-sources";
    }

}
