package com.feiliks.dashboard.monitors;

import com.feiliks.dashboard.spring.AbstractMonitorNotifier;
import com.feiliks.dashboard.SysInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class LocalCpuUsageMonitor extends AbstractMonitorNotifier {

    public static class CpuUsage {
        private long time;
        private Map<String, Double> data;

        CpuUsage(long time, Map<String, Double> data) {
            this.time = time;
            this.data = data;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public Map<String, Double> getData() {
            return data;
        }

        public void setData(Map<String, Double> data) {
            this.data = data;
        }
    }

    private Map<String, Long[]> lastCpuTime = null;
    private final static int HISTORY_LENGTH = 100;
    private final Queue<CpuUsage> history = new ConcurrentLinkedQueue<>();

    @Override
    public void run() {
        long curTime = System.currentTimeMillis();
        Map<String, Double> curMsr = new HashMap<>();

        Map<String, Long[]> cpuTime = SysInfo.getCPUUsage();
        if (lastCpuTime != null && cpuTime != null) {
            StringBuilder out = new StringBuilder();
            for (Map.Entry<String, Long[]> entry : cpuTime.entrySet()) {
                Long[] prev = lastCpuTime.get(entry.getKey());
                Long[] cur = entry.getValue();
                long total = cur[0] - prev[0];
                long used = cur[1] - prev[1];
                double measure = Math.round(10000.0 * used / total) / 100.0;
                curMsr.put(entry.getKey(), measure);
                out.append(entry.getKey())
                        .append('=')
                        .append(measure)
                        .append(',');
            }
            notifyClient(out.toString());
        }
        lastCpuTime = cpuTime;
        if (history.size() >= HISTORY_LENGTH)
            history.poll();
        history.offer(new CpuUsage(curTime, curMsr));

        exportDataSource("history", history);
    }

}
