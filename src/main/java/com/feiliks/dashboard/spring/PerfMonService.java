package com.feiliks.dashboard.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PerfMonService {

    @Autowired
    private WebSocketHandler wsPerformanceHandler;

    public void measure(String target, long timerStart) {
        long m = System.currentTimeMillis() - timerStart;
        long t = PerfMonitor.getInstance(target).measure(m);
        wsPerformanceHandler.broadcast(target + ':' + t + ':' + m);
    }

}
