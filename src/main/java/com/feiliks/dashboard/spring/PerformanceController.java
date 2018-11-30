package com.feiliks.dashboard.spring;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.Map;

@Controller
@RequestMapping("/performance")
public class PerformanceController {

    @GetMapping("/data/{target}")
    public ResponseEntity<Collection<Map.Entry<Long, Long>>> getMeasureData(
            @PathVariable("target") String target) {
        return ResponseEntity.ok(
                PerfMonitor.getInstance(target).getMeasureData());
    }

}
