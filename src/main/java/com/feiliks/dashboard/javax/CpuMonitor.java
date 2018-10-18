package com.feiliks.dashboard.javax;

import com.feiliks.dashboard.SysInfo;
import java.util.Map;

public class CpuMonitor extends DashboardServer.DashboardMonitorTask {

    private Map<String, Long[]> lastCpuTime = null;

    @Override
    public void run() {
        Map<String, Long[]> cpuTime = SysInfo.getCPUUsage();
        if (lastCpuTime != null && cpuTime != null) {
            StringBuilder out = new StringBuilder();
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
            broadcast("cpu", out.toString());
        }
        lastCpuTime = cpuTime;
    }

}
