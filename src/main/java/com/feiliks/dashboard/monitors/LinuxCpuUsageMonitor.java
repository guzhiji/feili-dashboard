package com.feiliks.dashboard.monitors;

import com.feiliks.dashboard.*;

import java.util.HashMap;
import java.util.Map;


public class LinuxCpuUsageMonitor extends AbstractMonitor {

    public final class Task extends AbstractMonitor.Task {

        private final FluctuationHistory<Map<String, Double>, Double> fluct = new FluctuationHistory<>(this);

        private Map<String, Long[]> lastCpuTime = null;

        @Override
        public void run() {
            long curTime = System.currentTimeMillis();
            Map<String, Double> curMsr = new HashMap<>();

            Map<String, Long[]> cpuTime = SysInfo.getCPUUsage();
            if (lastCpuTime != null && cpuTime != null) {
                for (Map.Entry<String, Long[]> entry : cpuTime.entrySet()) {
                    Long[] prev = lastCpuTime.get(entry.getKey());
                    Long[] cur = entry.getValue();
                    long total = cur[0] - prev[0];
                    long used = cur[1] - prev[1];
                    double measure = Math.round(10000.0 * used / total) / 100.0;
                    curMsr.put(entry.getKey(), measure);
                }
            }
            lastCpuTime = cpuTime;
            fluct.add(curTime, curMsr);

            exportResult("Status", curMsr);
            fluct.exportResults();
        }

    }

    public LinuxCpuUsageMonitor() {
        super(LinuxCpuUsageMonitor.class, Task.class, true);
        registerMessageSource("Fluct_Realtime", "obj-list");
        registerMessageSource("Fluct_cpu_Minutely", "obj-list");
        registerMessageSource("Fluct_cpu_Hourly", "obj-list");
        registerResultSource("Status", "obj");
        registerResultSource("Fluct_Realtime", "obj-list");
        registerResultSource("Fluct_cpu_Minutely", "obj-list");
        registerResultSource("Fluct_cpu_Hourly", "obj-list");
    }

}
