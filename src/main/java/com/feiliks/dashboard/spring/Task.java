package com.feiliks.dashboard.spring;

import java.util.concurrent.ScheduledFuture;

public class Task {

    private final Runnable runnable;
    private ScheduledFuture<?> monitorScheduledFuture;
    private Thread notifierThread;

    public Task(Runnable runnable) {
        this.runnable = runnable;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public ScheduledFuture<?> getMonitorScheduledFuture() {
        return monitorScheduledFuture;
    }

    public void setMonitorScheduledFuture(ScheduledFuture<?> monitorScheduledFuture) {
        this.monitorScheduledFuture = monitorScheduledFuture;
    }

    public Thread getNotifierThread() {
        return notifierThread;
    }

    public void setNotifierThread(Thread notifierThread) {
        this.notifierThread = notifierThread;
    }
}
