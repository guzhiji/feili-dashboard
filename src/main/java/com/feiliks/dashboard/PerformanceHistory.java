package com.feiliks.dashboard;

import java.util.HashMap;
import java.util.Map;


public class PerformanceHistory extends History<Map<String, Long>, Long> {

    private final AbstractMonitor.Task task;

    public PerformanceHistory(AbstractMonitor.Task task) {
        super(new IRealtimeEventHandler<Map<String, Long>>() {
            @Override
            public void onExpired(Item<Map<String, Long>> item) {
                task.sendMessage("Perf_Realtime", new NotifierMessage<>(
                        "remove", String.valueOf(item.getKey()), null));
            }

            @Override
            public void onNew(Item<Map<String, Long>> item) {
                task.sendMessage("Perf_Realtime", new NotifierMessage<>(
                        "update", String.valueOf(item.getKey()), item.getData()));
            }
        }, new IAggHistoryEventHandler<Long>() {
            @Override
            public void onPeriodExpired(String attr, Item<AggValues<Long>> item) {
                task.sendMessage("Perf_" + attr + "_Minutely", new NotifierMessage<>(
                        "remove", String.valueOf(item.getKey()), null));
            }

            @Override
            public void onNewPeriod(String attr, Item<AggValues<Long>> item) {
                task.sendMessage("Perf_" + attr + "_Minutely", new NotifierMessage<>(
                        "update", String.valueOf(item.getKey()), item.getData()));
            }
        }, new IAggHistoryEventHandler<Long>() {
            @Override
            public void onPeriodExpired(String attr, Item<AggValues<Long>> item) {
                task.sendMessage("Perf_" + attr + "_Hourly", new NotifierMessage<>(
                        "remove", String.valueOf(item.getKey()), null));
            }

            @Override
            public void onNewPeriod(String attr, Item<AggValues<Long>> item) {
                task.sendMessage("Perf_" + attr + "_Hourly", new NotifierMessage<>(
                        "update", String.valueOf(item.getKey()), item.getData()));
            }
        });
        this.task = task;
    }

    private Map<String, Long> lastTimestamps = null;
    private Map<String, Long> latencies = null;

    public void reset() {
        lastTimestamps = new HashMap<>();
        latencies = new HashMap<>();
    }

    public void start(String timer) {
        if (lastTimestamps != null)
            lastTimestamps.put(timer, System.currentTimeMillis());
    }

    public void stop(String timer) {
        if (lastTimestamps != null) {
            Long lts = lastTimestamps.get(timer);
            if (lts != null) {
                latencies.put(timer,
                        System.currentTimeMillis() - lts);
            }
        }
    }

    public void finish() {
        if (latencies != null)
            add(System.currentTimeMillis(), latencies);
        lastTimestamps = null;
        latencies = null;
    }

    public void exportResults() {
        task.exportResult("Perf_Realtime", getRealtimeData());
        for (String attr : getAttributes()) {
            task.exportResult("Perf_" + attr + "_Minutely",
                    getMinutelyData(attr));
            task.exportResult("Perf_" + attr + "_Hourly",
                    getHourlyData(attr));
        }
    }

}

