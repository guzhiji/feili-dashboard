package com.feiliks.dashboard.monitors;

import com.feiliks.dashboard.AbstractMonitor;
import com.feiliks.dashboard.FluctuationHistory;
import com.feiliks.dashboard.SysInfo;


public class LinuxMemoryUsageMonitor extends AbstractMonitor {

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

    public final class Task extends AbstractMonitor.Task {

        private final FluctuationHistory<MemoryUsage, Long> fluct = new FluctuationHistory<>(this);

        @Override
        public void run() {
            long[] memUsage = SysInfo.getMemoryUsage();
            if (memUsage != null) {

                long ts = System.currentTimeMillis();
                MemoryUsage mu = new MemoryUsage(memUsage[0], memUsage[1]);
                fluct.add(ts, mu);

                exportResult("Status", mu);
                fluct.exportResults();
            }
        }

    }

    public LinuxMemoryUsageMonitor() {
        super(LinuxMemoryUsageMonitor.class, Task.class, true);
        registerMessageSource("Fluct_Realtime", "obj-list");
        registerMessageSource("Fluct_Used_Minutely", "obj-list");
        registerMessageSource("Fluct_Available_Minutely", "obj-list");
        registerMessageSource("Fluct_Used_Hourly", "obj-list");
        registerMessageSource("Fluct_Available_Hourly", "obj-list");
        registerResultSource("Status", "obj");
        registerResultSource("Fluct_Realtime", "obj-list");
        registerResultSource("Fluct_Used_Minutely", "obj-list");
        registerResultSource("Fluct_Available_Minutely", "obj-list");
        registerResultSource("Fluct_Used_Hourly", "obj-list");
        registerResultSource("Fluct_Available_Hourly", "obj-list");
    }

}
