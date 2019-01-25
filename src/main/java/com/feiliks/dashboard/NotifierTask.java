package com.feiliks.dashboard;


public final class NotifierTask {

    private final INotifier runnable;
    private Thread notifierThread;

    public NotifierTask(INotifier runnable) {
        this.runnable = runnable;
    }

    public INotifier getRunnable() {
        return runnable;
    }

    public Thread getNotifierThread() {
        return notifierThread;
    }

    public void setNotifierThread(Thread notifierThread) {
        this.notifierThread = notifierThread;
    }

}
