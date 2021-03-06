package com.feiliks.dashboard.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class PerfMonService {

    @Autowired
    private SimpMessagingTemplate wsPerformance;

    private void broadcast(String msg) {
        wsPerformance.convertAndSend("/dashboard/performance", msg);
    }

    public void measure(String target, long timerStart) {
        long m = System.currentTimeMillis() - timerStart;
        long t = PerfMonitor.getInstance(target).measure(m);
        broadcast(target + ':' + t + ':' + m);
    }

}
