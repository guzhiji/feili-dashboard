package com.feiliks.dashboard.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/consolidation")
public class ConsolidationController {

    @Autowired
    private ConsolidationDao dao;

    @Autowired
    private ConsolidationTask task;

    @GetMapping
    public String show() {
        return "consolidation";
    }

    @GetMapping("/table.json")
    public ResponseEntity<List<ConsolidationDao.OrderTrolley>> getTable() {
        List<ConsolidationDao.OrderTrolley> table = task.getTable();
        if (table == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(table);
    }

    @GetMapping("/status.json")
    public ResponseEntity<Map<String, Long>> getStatus() {
        return ResponseEntity.ok(task.getCurrentStats());
    }

    @GetMapping("/history.json")
    public ResponseEntity<List<ConsolidationTask.HourlyStats>> getHistory() {
        return ResponseEntity.ok(task.getHistoricalStats());
    }

}
