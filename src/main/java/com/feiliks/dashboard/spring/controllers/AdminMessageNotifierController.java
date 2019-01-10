package com.feiliks.dashboard.spring.controllers;


import com.feiliks.dashboard.spring.repositories.MessageNotifierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/admin/message-notifiers")
public class AdminMessageNotifierController {

    @Autowired
    private MessageNotifierRepository notifierRepo;

}
