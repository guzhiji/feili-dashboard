package com.feiliks.dashboard.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/shipment")
public class ShipmentController {

    @Autowired
    private ShipmentDao dao;

    @GetMapping
    public String show() {
        return "shipment";
    }
}
