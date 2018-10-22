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


    @Scheduled(fixedDelay = 2000)
    public void pushCpuUsage() {
        Map<String, Long[]> cpuTime = SysInfo.getCPUUsage();
        if (lastCpuTime != null && cpuTime != null) {
            StringBuilder out = new StringBuilder("cpu:");
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
            webSocketHandler.broadcast(out.toString());
        }

        lastCpuTime = cpuTime;
    }

    @Scheduled(fixedDelay = 1000)
    public void pushMemoryUsage() {
        long[] memUsage = SysInfo.getMemoryUsage();
        if (memUsage != null)
            webSocketHandler.broadcast("mem:" + memUsage[0] + ',' + memUsage[1]);
    }

    @Scheduled(fixedDelay = 2000)
    public void pushNetworkRxTx() {
        Map<String, Long[]> ifaces = SysInfo.getIfaces();
        double curTime = System.currentTimeMillis() / 1000;
        if (ifaces != null && lastRxTx != null && curTime > lastRxTxTime) {
            StringBuilder out = new StringBuilder("net:");
            double timeDiff = curTime - lastRxTxTime;
            for (Map.Entry<String, Long[]> iface : ifaces.entrySet()) {
                Long[] prev = lastRxTx.get(iface.getKey());
                Long[] cur = iface.getValue();
                out.append(iface.getKey())
                        .append('=')
                        .append((cur[0] > prev[0]) ? ((cur[0] - prev[0]) / timeDiff) : 0.0)
                        .append(',')
                        .append((cur[1] > prev[1]) ? ((cur[1] - prev[1]) / timeDiff) : 0.0)
                        .append(';');
            }
            webSocketHandler.broadcast(out.toString());
        }
        lastRxTx = ifaces;
        lastRxTxTime = curTime;
    }

    @Scheduled(fixedDelay = 2000)
    public void pushDiskIO() {
        Map<String, Long[]> disks = SysInfo.getDiskIO();
        double curTime = System.currentTimeMillis() / 1000.0;
        if (disks != null && lastIO != null && curTime > lastDiskIOTime) {
            StringBuilder out = new StringBuilder("disk:");
            double timeDiff = curTime - lastDiskIOTime;
            for (Map.Entry<String, Long[]> disk : disks.entrySet()) {
                Long[] prev = lastIO.get(disk.getKey());
                Long[] cur = disk.getValue();
                out.append(disk.getKey())
                        .append('=')
                        .append((cur[0] > prev[0]) ? ((cur[0] - prev[0]) / timeDiff) : 0.0)
                        .append(',')
                        .append((cur[1] > prev[1]) ? ((cur[1] - prev[1]) / timeDiff) : 0.0)
                        .append(';');
            }
            webSocketHandler.broadcast(out.toString());
        }
        lastIO = disks;
        lastDiskIOTime = curTime;
    }

}
