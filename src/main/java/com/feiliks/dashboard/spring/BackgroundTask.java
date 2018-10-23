package com.feiliks.dashboard.spring;


import com.feiliks.dashboard.SysInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BackgroundTask {

    @Autowired
    private WebSocketHandler webSocketHandler;

    private Map<String, Long[]> lastCpuTime = null;
    private Map<String, Long[]> lastRxTx = null;
    private double lastRxTxTime;
    private Map<String, Long[]> lastIO = null;
    private double lastDiskIOTime;

    private void broadcast(String type, String msg) {
        webSocketHandler.broadcast(type + ":" + System.currentTimeMillis() + ":" + msg);
    }

    @Scheduled(fixedDelay = 2000)
    public void pushCpuUsage() {
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
                        .append(Math.round(10000.0 * used / total) / 100.0)
                        .append(',');
            }
            broadcast("cpu", out.toString());
        }

        lastCpuTime = cpuTime;
    }

    @Scheduled(fixedDelay = 1000)
    public void pushMemoryUsage() {
        long[] memUsage = SysInfo.getMemoryUsage();
        if (memUsage != null)
            broadcast("mem", memUsage[0] + "," + memUsage[1]);
    }

    @Scheduled(fixedDelay = 2000)
    public void pushNetworkRxTx() {
        Map<String, Long[]> ifaces = SysInfo.getIfaces();
        double curTime = System.currentTimeMillis() / 1000.0;
        if (ifaces != null && lastRxTx != null && curTime > lastRxTxTime) {
            StringBuilder out = new StringBuilder();
            double timeDiff = curTime - lastRxTxTime;
            for (Map.Entry<String, Long[]> iface : ifaces.entrySet()) {
                Long[] prev = lastRxTx.get(iface.getKey());
                Long[] cur = iface.getValue();
                out.append(iface.getKey())
                        .append('=')
                        .append((cur[0] > prev[0]) ? Math.round((cur[0] - prev[0]) / timeDiff) : 0.0)
                        .append(',')
                        .append((cur[1] > prev[1]) ? Math.round((cur[1] - prev[1]) / timeDiff) : 0.0)
                        .append(';');
            }
            broadcast("net", out.toString());
        }
        lastRxTx = ifaces;
        lastRxTxTime = curTime;
    }

    @Scheduled(fixedDelay = 2000)
    public void pushDiskIO() {
        Map<String, Long[]> disks = SysInfo.getDiskIO();
        double curTime = System.currentTimeMillis() / 1000.0;
        if (disks != null && lastIO != null && curTime > lastDiskIOTime) {
            StringBuilder out = new StringBuilder();
            double timeDiff = curTime - lastDiskIOTime;
            for (Map.Entry<String, Long[]> disk : disks.entrySet()) {
                Long[] prev = lastIO.get(disk.getKey());
                Long[] cur = disk.getValue();
                out.append(disk.getKey())
                        .append('=')
                        .append((cur[0] > prev[0]) ? Math.round((cur[0] - prev[0]) / timeDiff) : 0.0)
                        .append(',')
                        .append((cur[1] > prev[1]) ? Math.round((cur[1] - prev[1]) / timeDiff) : 0.0)
                        .append(';');
            }
            broadcast("disk", out.toString());
        }
        lastIO = disks;
        lastDiskIOTime = curTime;
    }

}
