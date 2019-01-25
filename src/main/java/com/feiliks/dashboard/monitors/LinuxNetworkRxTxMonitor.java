package com.feiliks.dashboard.monitors;

import com.feiliks.dashboard.SysInfo;
import com.feiliks.dashboard.spring.impl.AbstractMonitorNotifier;

import java.util.Map;


public class LinuxNetworkRxTxMonitor extends AbstractMonitorNotifier {

    private Map<String, Long[]> lastRxTx = null;
    private double lastRxTxSecs;

    @Override
    public void run() {
        Map<String, Long[]> ifaces = SysInfo.getIfaces();
        double curSecs = System.currentTimeMillis() / 1000.0;
        if (ifaces != null && lastRxTx != null && curSecs > lastRxTxSecs) {
            StringBuilder out = new StringBuilder();
            double secsDiff = curSecs - lastRxTxSecs;
            for (Map.Entry<String, Long[]> iface : ifaces.entrySet()) {
                Long[] prev = lastRxTx.get(iface.getKey());
                Long[] cur = iface.getValue();
                out.append(iface.getKey())
                        .append('=')
                        .append((cur[0] > prev[0]) ? Math.round((cur[0] - prev[0]) / secsDiff) : 0.0)
                        .append(',')
                        .append((cur[1] > prev[1]) ? Math.round((cur[1] - prev[1]) / secsDiff) : 0.0)
                        .append(';');
            }
            notifyClient(out.toString());
        }
        lastRxTx = ifaces;
        lastRxTxSecs = curSecs;
    }

}
