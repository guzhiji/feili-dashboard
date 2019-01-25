package com.feiliks.dashboard;

import java.util.concurrent.ScheduledFuture;


public final class MonitorTask {

    private final IMonitor runnable;
    private ScheduledFuture<?> scheduledFuture;

    public MonitorTask(IMonitor runnable) {
        this.runnable = runnable;
    }

    public IMonitor getRunnable() {
        return runnable;
    }

    public ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }

    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

}
