package com.feiliks.dashboard.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/performance")
public class PerformanceController {

    private final static String[] MONITOR_TARGETS = {
            ConsolidationDao.PERFMON_KEY,
            ShipmentDao.PERFMON_TROLLEYS,
            ShipmentDao.PERFMON_TROLLEY_ORDER,
            ShipmentDao.PERFMON_APPOINTMENTS
    };

    @Autowired
    private SimpMessagingTemplate wsPerformance;

    @GetMapping
    public String show() {
        return "performance";
    }

    @PostMapping("/reload")
    public ResponseEntity<?> sendReloadCmd() {
        wsPerformance.convertAndSend("/dashboard/performance", "reload");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/data/realtime.json")
    public ResponseEntity<Map<String, Collection<Map.Entry<Long, Long>>>> getMeasureData() {
        Map<String, Collection<Map.Entry<Long, Long>>> data = new HashMap<>();
        for (String t : MONITOR_TARGETS) {
            if (PerfMonitor.exists(t))
                data.put(t, PerfMonitor.getInstance(t).getMeasureData());
        }
        return ResponseEntity.ok(data);
    }

    @GetMapping("/data/minutely.json")
    public ResponseEntity<Map<String, Collection<PerfMonitor.AggInfo>>> getMinutelyData() {
        Map<String, Collection<PerfMonitor.AggInfo>> data = new HashMap<>();
        for (String t : MONITOR_TARGETS) {
            if (PerfMonitor.exists(t))
                data.put(t, PerfMonitor.getInstance(t).getMinutelyData());
        }
        return ResponseEntity.ok(data);
    }

    @GetMapping("/data/hourly.json")
    public ResponseEntity<Map<String, Collection<PerfMonitor.AggInfo>>> getHourlyData() {
        Map<String, Collection<PerfMonitor.AggInfo>> data = new HashMap<>();
        for (String t : MONITOR_TARGETS) {
            if (PerfMonitor.exists(t))
                data.put(t, PerfMonitor.getInstance(t).getHourlyData());
        }
        return ResponseEntity.ok(data);
    }

}
