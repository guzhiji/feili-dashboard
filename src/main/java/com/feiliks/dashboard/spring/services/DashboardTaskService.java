package com.feiliks.dashboard.spring.services;

import com.feiliks.dashboard.*;
import com.feiliks.dashboard.spring.*;
import com.feiliks.dashboard.spring.entities.MessageNotifierEntity;
import com.feiliks.dashboard.spring.entities.MonitorEntity;
import com.feiliks.dashboard.spring.impl.Messenger;
import com.feiliks.dashboard.spring.impl.MonitorData;
import com.feiliks.dashboard.spring.impl.NotifierData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;


@Service
public class DashboardTaskService {

    @Autowired
    private SimpMessagingTemplate messaging;

    @Autowired
    private IDbConnManager dbConnManager;

    @Autowired
    private ThreadPoolTaskScheduler monitorScheduler;

    private final Map<Long, MonitorTask> runningMonitors = new HashMap<>();
    private final Map<Long, NotifierTask> runningNotifiers = new HashMap<>();

    public IMonitor getMonitorTask(MonitorEntity entity) {
        MonitorTask task = runningMonitors.get(entity.getId());
        if (task == null) return null;
        return task.getRunnable();
    }

    public INotifier getNotifierTask(MessageNotifierEntity entity) {
        NotifierTask task = runningNotifiers.get(entity.getId());
        if (task == null) return null;
        return task.getRunnable();
    }

    private Runnable instantiateRunnable(String javaClass)
            throws TaskActivationException.RunnableNotInstantiated {
        try {
            Class<?> cls = Class.forName(javaClass);
            return (Runnable) cls.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new TaskActivationException.RunnableNotInstantiated(e);
        }
    }

    public synchronized boolean activate(MonitorEntity monitor)
            throws TaskActivationException {
        if (!runningMonitors.containsKey(monitor.getId())) {
            Runnable runnable = instantiateRunnable(
                    monitor.getJavaClass());
            activate(runnable, monitor, null);
            return true;
        }
        return false;
    }

    public synchronized boolean activate(MessageNotifierEntity notifier)
            throws TaskActivationException {
        if (!runningMonitors.containsKey(notifier.getId())) {
            Runnable runnable = instantiateRunnable(
                    notifier.getJavaClass());
            activate(runnable, null, notifier);
            return true;
        }
        return false;
    }

    public synchronized boolean activate(MonitorEntity monitor, MessageNotifierEntity notifier)
            throws TaskActivationException {
        if (!runningMonitors.containsKey(monitor.getId())) {
            Runnable runnable = instantiateRunnable(
                    monitor.getJavaClass());
            activate(runnable, monitor, notifier);
            return true;
        }
        return false;
    }

    private void activate(Runnable runnable, MonitorEntity monitorEntity, MessageNotifierEntity notifierEntity)
            throws TaskActivationException {

        Class<?> javaClass = runnable.getClass();
        String className = runnable.getClass().getCanonicalName();

        boolean activated = false;
        if (IMonitor.class.isAssignableFrom(javaClass)) {
            if (monitorEntity == null)
                throw new TaskActivationException.MissingDataEntity();
            if (!className.equals(monitorEntity.getJavaClass()))
                throw new TaskActivationException.InconsistentClass();

            // initialize monitor
            IMonitor monitor = (IMonitor) runnable;
            monitor.initMonitor(new MonitorData(monitorEntity), dbConnManager);

            // schedule task
            MonitorTask task = new MonitorTask(monitor);
            ScheduledFuture<?> future = monitorScheduler.scheduleAtFixedRate(
                    monitor, monitorEntity.getExecRate());
            task.setScheduledFuture(future);

            runningMonitors.put(monitorEntity.getId(), task);
            activated = true;
        }
        if (INotifier.class.isAssignableFrom(javaClass)) {
            if (notifierEntity == null)
                throw new TaskActivationException.MissingDataEntity();
            if (!className.equals(notifierEntity.getJavaClass()))
                throw new TaskActivationException.InconsistentClass();

            // initialize notifier
            INotifier notifier = (INotifier) runnable;
            notifier.initNotifier(
                    new NotifierData(notifierEntity),
                    new Messenger(notifierEntity.getId(), messaging));

            if (!activated) {
                // start task
                NotifierTask task = new NotifierTask(notifier);
                Thread notifierThread = new Thread(notifier);
                notifierThread.start();
                task.setNotifierThread(notifierThread);

                runningNotifiers.put(notifierEntity.getId(), task);
                activated = true;
            }
        }
        if (!activated)
            throw new TaskActivationException.NonSupportedTask();
    }

    public synchronized void deactivate(MonitorEntity monitor) {
        if (monitor != null) {
            long id = monitor.getId();
            MonitorTask task = runningMonitors.get(id);
            if (task != null) {
                ScheduledFuture<?> sf = task.getScheduledFuture();
                if (sf != null)
                    sf.cancel(true);
                runningMonitors.remove(id);
            }
        }
    }

    public synchronized void deactivate(MessageNotifierEntity notifier) {
        if (notifier != null) {
            long id = notifier.getId();
            NotifierTask task = runningNotifiers.get(id);
            if (task != null) {
                Thread t = task.getNotifierThread();
                if (t != null)
                    t.interrupt();
                runningNotifiers.remove(id);
            }
        }
    }

}
