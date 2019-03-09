package com.feiliks.dashboard.monitors;

import com.feiliks.dashboard.History;
import com.feiliks.dashboard.NotifierMessage;
import com.feiliks.dashboard.SysInfo;
import com.feiliks.dashboard.AbstractMonitor;

import java.util.HashMap;
import java.util.Map;


public class LinuxCpuUsageMonitor extends AbstractMonitor {

    public final class Task extends AbstractMonitor.Task {

        private final History<Map<String, Double>, Double> history = new History<>(
                new History.IRealtimeEventHandler<Map<String, Double>>() {
                    @Override
                    public void onExpired(History.Item<Map<String, Double>> item) {
                        notifyClient("History_Realtime", new NotifierMessage<>(
                                        "remove", String.valueOf(item.getTime()), null));
                    }

                    @Override
                    public void onNew(History.Item<Map<String, Double>> item) {
                        notifyClient("History_Realtime", new NotifierMessage<>(
                                "update", String.valueOf(item.getTime()), item.getData()));
                    }
                },
                new History.IAggHistoryEventHandler<Double>() {
                    @Override
                    public void onPeriodExpired(String attr, History.Item<History.AggValues<Double>> item) {

                    }

                    @Override
                    public void onNewPeriod(String attr, History.Item<History.AggValues<Double>> item) {

                    }
                },
                new History.IAggHistoryEventHandler<Double>() {
                    @Override
                    public void onPeriodExpired(String attr, History.Item<History.AggValues<Double>> item) {

                    }

                    @Override
                    public void onNewPeriod(String attr, History.Item<History.AggValues<Double>> item) {

                    }
                });

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
                notifyClient("Status", new NotifierMessage<>(
                        "update", String.valueOf(curTime), curMsr));
            }
            lastCpuTime = cpuTime;
            history.add(curTime, curMsr);

            exportResult("Status", curMsr);
            exportResult("History_Realtime", history.getRealtimeData());
        }

    }

    public LinuxCpuUsageMonitor() {
        super(LinuxCpuUsageMonitor.class, Task.class, true);
        registerNotificationSource("History_Realtime", "map");
        registerResultSource("Status", "map");
        registerResultSource("History_Realtime", "");
    }

}
