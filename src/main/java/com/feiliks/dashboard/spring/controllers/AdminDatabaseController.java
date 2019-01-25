package com.feiliks.dashboard.spring.controllers;

import com.feiliks.dashboard.spring.NotFoundException;
import com.feiliks.dashboard.spring.dto.DatabaseFormDto;
import com.feiliks.dashboard.spring.entities.DatabaseEntity;
import com.feiliks.dashboard.spring.impl.DatabaseInfo;
import com.feiliks.dashboard.spring.repositories.DatabaseRepository;
import com.feiliks.dashboard.spring.services.DbConnManager;
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
@RequestMapping("/admin/databases")
public class AdminDatabaseController {

    @Autowired
    private DatabaseRepository dbRepo;

    @Autowired
    private DbConnManager dbConnManager;

    @GetMapping
    public ModelAndView showDatabases() {
        Map<String, Object> data = new HashMap<>();
        data.put("list", dbRepo.findAll());
        return new ModelAndView("admin/database/list", data);
    }

    @PostMapping
    public String createDatabase(
            @Valid DatabaseFormDto formData,
            BindingResult vresult,
            RedirectAttributes ratts) {
        if (vresult.hasErrors()) {
            FieldError fe = vresult.getFieldError();
            if (fe != null)
                ratts.addFlashAttribute("flashMessage", fe.getDefaultMessage());
            return "redirect:/admin/databases/new";
        }
        dbRepo.save(formData.toEntity());
        ratts.addFlashAttribute("flashMessage", "database-saved");
        return "redirect:/admin/databases";
    }

    @GetMapping("/new")
    public ModelAndView showDatabaseEditor() {
        Map<String, Object> data = new HashMap<>();
        data.put("mode", "create");
        data.put("saveUrl", "/admin/databases");
        return new ModelAndView("admin/database/edit", data);
    }

    @GetMapping("{id}")
    public ModelAndView showDatabaseEditor(@PathVariable long id)
            throws NotFoundException {
        DatabaseEntity entity = dbRepo.findById(id)
                .orElseThrow(NotFoundException::new);

        Map<String, Object> data = new HashMap<>();
        data.put("mode", "modify");
        data.put("saveUrl", "/admin/databases/" + id);
        data.put("entity", entity);

        return new ModelAndView("admin/database/edit", data);
    }

    @PostMapping("/{id}")
    public String modifyDatabase(
            @PathVariable long id,
            @Valid DatabaseFormDto formData,
            BindingResult vresult,
            RedirectAttributes ratts)
            throws NotFoundException {
        if (vresult.hasErrors()) {
            FieldError fe = vresult.getFieldError();
            if (fe != null)
                ratts.addFlashAttribute("flashMessage", fe.getDefaultMessage());
            return "redirect:/admin/databases/" + id;
        }
        DatabaseEntity entity = dbRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        formData.toEntity(entity);
        entity.setId(id);
        dbConnManager.updateDbInfo(new DatabaseInfo(entity));
        dbRepo.save(entity);
        ratts.addFlashAttribute("flashMessage", "database-saved");
        return "redirect:/admin/databases";
    }

    @PostMapping("/{id}/delete")
    public String deleteDatabase(
            @PathVariable long id,
            RedirectAttributes ratts) {
        dbConnManager.removeDb(id);
        dbRepo.deleteById(id);
        ratts.addFlashAttribute("flashMessage", "database-deleted");
        return "redirect:/admin/databases";
    }

}
