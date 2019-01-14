package com.feiliks.dashboard.spring.controllers;


import com.feiliks.dashboard.spring.NotFoundException;
import com.feiliks.dashboard.spring.dto.MessageNotifierFormDto;
import com.feiliks.dashboard.spring.entities.MessageNotifierEntity;
import com.feiliks.dashboard.spring.repositories.MessageNotifierRepository;
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
@RequestMapping("/admin/message-notifiers")
public class AdminMessageNotifierController {

    @Autowired
    private MessageNotifierRepository notifierRepo;

    @GetMapping
    public ModelAndView listMessageNotifiers() {
        Map<String, Object> data = new HashMap<>();
        data.put("list", notifierRepo.findAll());
        return new ModelAndView("admin/message-notifier/list", data);
    }

    @PostMapping
    public String createMessageNotifier(
            @Valid MessageNotifierFormDto formData,
            BindingResult vresult,
            RedirectAttributes ratts) {

        if (vresult.hasErrors()) {
            FieldError fe = vresult.getFieldError();
            if (fe != null)
                ratts.addFlashAttribute("flashMessage", fe.getDefaultMessage());
            return "redirect:/admin/message-notifiers/new";
        }

        MessageNotifierEntity entity = formData.toEntity();
        notifierRepo.save(entity);
        ratts.addFlashAttribute("flashMessage", "notifier-saved");

        return "redirect:/admin/message-notifiers";
    }

    @GetMapping("/new")
    public ModelAndView showMessageNotifierEditor() {
        Map<String, Object> data = new HashMap<>();
        data.put("mode", "create");
        data.put("saveUrl", "/admin/message-notifiers");
        return new ModelAndView("admin/message-notifier/edit", data);
    }

    @GetMapping("/{id}")
    public ModelAndView showMessageNotifierEditor(@PathVariable long id)
            throws NotFoundException {
        MessageNotifierEntity entity = notifierRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        Map<String, Object> data = new HashMap<>();
        data.put("mode", "modify");
        data.put("saveUrl", "/admin/message-notifiers/" + id);
        data.put("entity", entity);
        return new ModelAndView("admin/message-notifier/edit", data);
    }

    @PostMapping("/{id}")
    public String modifyMessageNotifier(
            @PathVariable long id,
            @Valid MessageNotifierFormDto formData,
            BindingResult vresult,
            RedirectAttributes ratts)
            throws NotFoundException {

        if (vresult.hasErrors()) {
            FieldError fe = vresult.getFieldError();
            if (fe != null)
                ratts.addFlashAttribute("flashMessage", fe.getDefaultMessage());
            return "redirect:/admin/message-notifiers/" + id;
        }

        MessageNotifierEntity entity = notifierRepo.findById(id)
                .orElseThrow(NotFoundException::new);
        formData.toEntity(entity);
        entity.setId(id);

        notifierRepo.save(entity);
        ratts.addFlashAttribute("flashMessage", "notifier-saved");

        return "redirect:/admin/message-notifiers";
    }

    @PostMapping("/{id}/delete")
    public String deleteMessageNotifier(
            @PathVariable long id,
            RedirectAttributes ratts) {

        notifierRepo.deleteById(id);
        ratts.addFlashAttribute("flashMessage", "notifier-deleted");

        return "redirect:/admin/message-notifiers";
    }

}
