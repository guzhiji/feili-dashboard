package com.feiliks.dashboard.monitors;

import com.feiliks.dashboard.History;
import com.feiliks.dashboard.NotifierMessage;
import com.feiliks.dashboard.SysInfo;
import com.feiliks.dashboard.AbstractMonitor;


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

        private final History<MemoryUsage, Long> history = new History<>(
                new History.IRealtimeEventHandler<MemoryUsage>() {
                    @Override
                    public void onExpired(History.Item<MemoryUsage> item) {

                    }

                    @Override
                    public void onNew(History.Item<MemoryUsage> item) {
                        sendMessage("History_Realtime", new NotifierMessage<>(
                                "update",
                                String.valueOf(item.getTime()),
                                item.getData()));
                    }
                },
                new History.IAggHistoryEventHandler<Long>() {
                    @Override
                    public void onPeriodExpired(String attr, History.Item<History.AggValues<Long>> item) {

                    }

                    @Override
                    public void onNewPeriod(String attr, History.Item<History.AggValues<Long>> item) {
                        sendMessage("History_" + attr + "_Minutely", new NotifierMessage<>(
                                "update",
                                String.valueOf(item.getTime()),
                                item.getData()));
                    }
                },
                new History.IAggHistoryEventHandler<Long>() {
                    @Override
                    public void onPeriodExpired(String attr, History.Item<History.AggValues<Long>> item) {

                    }

                    @Override
                    public void onNewPeriod(String attr, History.Item<History.AggValues<Long>> item) {
                        sendMessage("History_" + attr + "_Hourly", new NotifierMessage<>(
                                "update",
                                String.valueOf(item.getTime()),
                                item.getData()));
                    }
                });

        @Override
        public void run() {
            long[] memUsage = SysInfo.getMemoryUsage();
            if (memUsage != null) {

                long ts = System.currentTimeMillis();
                MemoryUsage mu = new MemoryUsage(memUsage[0], memUsage[1]);
                history.add(ts, mu);

                exportResult("Status", mu);
                exportResult(
                        "History_Realtime",
                        history.getRealtimeData());
                for (String attr : history.getAttributes()) {
                    exportResult(
                            "History_" + attr + "_Minutely",
                            history.getMinutelyData(attr));
                    exportResult(
                            "History_" + attr + "_Hourly",
                            history.getHourlyData(attr));
                }

            }
        }

    }

    public LinuxMemoryUsageMonitor() {
        super(LinuxMemoryUsageMonitor.class, Task.class, true);
        registerMessageSource("History_Realtime", "time-obj-list");
        registerMessageSource("History_Used_Minutely", "time-obj-list");
        registerMessageSource("History_Available_Minutely", "time-obj-list");
        registerMessageSource("History_Used_Hourly", "time-obj-list");
        registerMessageSource("History_Available_Hourly", "time-obj-list");
        registerResultSource("Status", "obj");
        registerResultSource("History_Realtime", "time-obj-list");
        registerResultSource("History_Used_Minutely", "time-obj-list");
        registerResultSource("History_Available_Minutely", "time-obj-list");
        registerResultSource("History_Used_Hourly", "time-obj-list");
        registerResultSource("History_Available_Hourly", "time-obj-list");
    }

}
