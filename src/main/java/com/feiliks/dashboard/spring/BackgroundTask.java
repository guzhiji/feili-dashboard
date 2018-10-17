package com.feiliks.dashboard.spring;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BackgroundTask {

    @Autowired
    private WebSocketHandler webSocketHandler;

    private Map<String, Long[]> lastCpuTime = null;


    @Scheduled(fixedDelay = 1000)
    public void pushData() {

        Map<String, Long[]> cpuTime = SysInfo.getCPUUsage();
        long[] memUsage = SysInfo.getMemoryUsage();
        if (memUsage != null && lastCpuTime != null && cpuTime != null) {
            StringBuilder out = new StringBuilder();
            out.append(memUsage[0])
                    .append(',')
                    .append(memUsage[1])
                    .append(';');
            for (Map.Entry<String, Long[]> entry : cpuTime.entrySet()) {
                Long[] prev = lastCpuTime.get(entry.getKey());
                Long[] cur = entry.getValue();
                long total = cur[0] - prev[0];
                long used = cur[1] - prev[1];
                out.append(entry.getKey())
                        .append(':')
                        .append(100.0 * used / total)
                        .append(',');
            }
            webSocketHandler.broadcast(out.toString());
        }

        lastCpuTime = cpuTime;
    }

}
