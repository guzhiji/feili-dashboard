package com.feiliks.dashboard.monitors;

import com.feiliks.dashboard.SysInfo;
import com.feiliks.dashboard.spring.impl.AbstractMonitorNotifier;

public class LocalMemoryUsageMonitor extends AbstractMonitorNotifier {

    public static class MemoryUsage {
        private long used;
        private long available;

        public MemoryUsage() {}

        public MemoryUsage(long used, long available) {
            this.used = used;
            this.available = available;
        }

        public long getUsed() {
            return used;
        }

        public void setUsed(long used) {
            this.used = used;
        }

        public long getAvailable() {
            return available;
        }

        public void setAvailable(long available) {
            this.available = available;
        }
    }

    @Override
    public void run() {
        long[] memUsage = SysInfo.getMemoryUsage();
        if (memUsage != null) {
            notifyClient(memUsage[0] + "," + memUsage[1]);
            exportDataSource(
                    "status",
                    new MemoryUsage(
                            memUsage[0], memUsage[1]));
        }
    }

}
