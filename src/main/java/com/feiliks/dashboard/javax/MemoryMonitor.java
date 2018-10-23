package com.feiliks.dashboard.javax;

import com.feiliks.dashboard.SysInfo;

public class MemoryMonitor extends DashboardMonitorTask {

    @Override
    public void run() {
        long[] memUsage = SysInfo.getMemoryUsage();
        if (memUsage != null) {
            broadcast("mem", memUsage[0] + "," + memUsage[1]);
        }
    }

}
