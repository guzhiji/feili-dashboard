package com.feiliks.dashboard.monitors;

import com.feiliks.dashboard.AbstractMonitor;
import com.feiliks.dashboard.PerformanceHistory;

import java.io.IOException;
import java.net.*;


public class WebRequestMonitor extends AbstractMonitor {

    public final class Task extends AbstractMonitor.Task {

        private final PerformanceHistory perf = new PerformanceHistory(this);

        @Override
        public void run() {
            String url = (String) getMonitorInfo().readConfig("url");
            Integer timeout = (Integer) getMonitorInfo().readConfig("timeout");
            if (timeout == null) timeout = 3000;
            perf.reset();
            perf.start("Conn");
            try {
                URL urlObj = new URL(url);
                URLConnection conn = urlObj.openConnection();
                conn.setConnectTimeout(timeout);
                conn.setDefaultUseCaches(false);
                conn.getContent();
                perf.stop("Conn");
                perf.finish();
                perf.exportResults();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public WebRequestMonitor() {
        super(WebRequestMonitor.class, Task.class, true);
        registerResultSource("Perf_Realtime", "obj-list");
        registerResultSource("Perf_Conn_Minutely", "obj-list");
        registerResultSource("Perf_Conn_Hourly", "obj-list");
        registerMessageSource("Perf_Realtime", "obj-list");
        registerMessageSource("Perf_Conn_Minutely", "obj-list");
        registerMessageSource("Perf_Conn_Hourly", "obj-list");
    }

}
