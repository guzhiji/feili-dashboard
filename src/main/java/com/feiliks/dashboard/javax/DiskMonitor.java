package com.feiliks.dashboard.javax;

import com.feiliks.dashboard.SysInfo;
import java.util.Map;

public class DiskMonitor extends DashboardServer.DashboardMonitorTask {
    private Map<String, Long[]> lastIO = null;
    private double lastTime;

    @Override
    public void run() {
        Map<String, Long[]> disks = SysInfo.getDiskIO();
        double curTime = System.currentTimeMillis() / 1000.0;
        if (disks != null && lastIO != null && curTime > lastTime) {
            StringBuilder out = new StringBuilder();
            double timeDiff = curTime - lastTime;
            for (Map.Entry<String, Long[]> disk : disks.entrySet()) {
                Long[] prev = lastIO.get(disk.getKey());
                Long[] cur = disk.getValue();
                out.append(disk.getKey())
                        .append('=')
                        .append((cur[0] > prev[0]) ? (Math.round((cur[0] - prev[0]) / timeDiff * 100) / 100.0) : 0.0)
                        .append(',')
                        .append((cur[1] > prev[1]) ? (Math.round((cur[1] - prev[1]) / timeDiff * 100) / 100.0) : 0.0)
                        .append(';');
            }
            broadcast("disk", out.toString());
        }
        lastIO = disks;
        lastTime = curTime;
    }

}
