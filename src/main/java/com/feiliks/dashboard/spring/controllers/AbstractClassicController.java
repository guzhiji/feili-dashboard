package com.feiliks.dashboard.spring.controllers;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public abstract class AbstractClassicController {

    protected boolean checkValidation(BindingResult vresult, RedirectAttributes ratts) {
        if (vresult.hasErrors()) {
            FieldError fe = vresult.getFieldError();
            if (fe != null) {
                ratts.addFlashAttribute("flashMessage", fe.getDefaultMessage());
            } else {
                ObjectError oe = vresult.getGlobalError();
                if (oe != null)
                    ratts.addFlashAttribute("flashMessage", oe.getDefaultMessage());
            }
            return false;
        }
        return true;
    }

}
