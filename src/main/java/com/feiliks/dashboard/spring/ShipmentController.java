package com.feiliks.dashboard.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/shipment")
public class ShipmentController {

    @Autowired
    private ShipmentTask task;

    @GetMapping
    public String show() {
        return "shipment";
    }

    @PostMapping("/reload")
    public ResponseEntity<?> sendReloadCmd() {
        task.sendReloadCmd();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/table.json")
    public ResponseEntity<List<ShipmentDao.Trolley>> getTable() {
        List<ShipmentDao.Trolley> table = task.getTable();
        if (table == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(table);
    }

    @GetMapping("/status.json")
    public ResponseEntity<Map<String, Integer>> getStatus() {
        return ResponseEntity.ok(task.getStatus());
    }

    @GetMapping("/appointments.json")
    public ResponseEntity<List<ShipmentDao.Appointment>> getAppointments() {
        return ResponseEntity.ok(task.getAppointments());
    }

}
