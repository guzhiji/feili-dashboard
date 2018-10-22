package com.feiliks.dashboard.javax;

import com.feiliks.dashboard.SysInfo;
import java.util.Map;

public class NetMonitor extends DashboardServer.DashboardMonitorTask {
    private Map<String, Long[]> lastRxTx = null;
    private double lastRxTxTime;

    @Override
    public void run() {
        Map<String, Long[]> ifaces = SysInfo.getIfaces();
        double curTime = System.currentTimeMillis() / 1000;
        if (ifaces != null && lastRxTx != null && curTime > lastRxTxTime) {
            StringBuilder out = new StringBuilder();
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
            broadcast("net", out.toString());
        }
        lastRxTx = ifaces;
        lastRxTxTime = curTime;
    }

}
