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
    public void pushCpuUsage() {
        Map<String, Long[]> cpuTime = SysInfo.getCPUUsage();
        if (lastCpuTime != null && cpuTime != null) {
            StringBuilder out = new StringBuilder("cpu:");
            for (Map.Entry<String, Long[]> entry : cpuTime.entrySet()) {
                Long[] prev = lastCpuTime.get(entry.getKey());
                Long[] cur = entry.getValue();
                long total = cur[0] - prev[0];
                long used = cur[1] - prev[1];
                out.append(entry.getKey())
                        .append('=')
                        .append(100.0 * used / total)
                        .append(',');
            }
            webSocketHandler.broadcast(out.toString());
        }

        lastCpuTime = cpuTime;
    }

    @Scheduled(fixedDelay = 1000)
    public void pushMemoryUsage() {
        long[] memUsage = SysInfo.getMemoryUsage();
	if (memUsage != null)
            webSocketHandler.broadcast("mem:" + memUsage[0] + ',' + memUsage[1]);
    }

}
