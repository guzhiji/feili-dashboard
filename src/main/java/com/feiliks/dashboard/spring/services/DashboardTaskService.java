package com.feiliks.dashboard.spring.services;

import com.feiliks.dashboard.IMonitor;
import com.feiliks.dashboard.INotifier;
import com.feiliks.dashboard.spring.*;
import com.feiliks.dashboard.spring.entities.MessageNotifierEntity;
import com.feiliks.dashboard.spring.entities.MonitorEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Service
public class DashboardTaskService {

    @Autowired
    private SimpMessagingTemplate messaging;

    @Autowired
    private ThreadPoolTaskScheduler dashboardScheduler;

    private final Map<String, Task> runningTasks = new HashMap<>();

    public IMonitor getMonitorTask(MonitorEntity entity) {
        if (entity == null)
            return null;
        return getMonitorTask(entity.getJavaClass());
    }

    public IMonitor getMonitorTask(String javaClass) {
        Task task = runningTasks.get(javaClass);
        if (task != null) {
            Runnable runnable = task.getRunnable();
            List<Class<?>> interfaces = Arrays.asList(
                    runnable.getClass().getInterfaces());
            if (interfaces.contains(IMonitor.class))
                return (IMonitor) runnable;
        }
        return null;
    }

    public INotifier getNotifierTask(MessageNotifierEntity entity) {
        if (entity == null)
            return null;
        return getNotifierTask(entity.getJavaClass());
    }

    public INotifier getNotifierTask(String javaClass) {
        Task task = runningTasks.get(javaClass);
        if (task != null) {
            Runnable runnable = task.getRunnable();
            List<Class<?>> interfaces = Arrays.asList(
                    runnable.getClass().getInterfaces());
            if (interfaces.contains(INotifier.class))
                return (INotifier) runnable;
        }
        return null;
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
        String javaClass = monitor.getJavaClass();
        if (!runningTasks.containsKey(javaClass)) {
            Runnable runnable = instantiateRunnable(javaClass);
            activate(runnable, monitor, null);
            return true;
        }
        return false;
    }

    public synchronized boolean activate(MessageNotifierEntity notifier)
            throws TaskActivationException {
        String javaClass = notifier.getJavaClass();
        if (!runningTasks.containsKey(javaClass)) {
            Runnable runnable = instantiateRunnable(javaClass);
            activate(runnable, null, notifier);
            return true;
        }
        return false;
    }

    public synchronized boolean activate(MonitorEntity monitor, MessageNotifierEntity notifier)
            throws TaskActivationException {
        String javaClass = monitor.getJavaClass();
        if (!runningTasks.containsKey(javaClass)) {
            Runnable runnable = instantiateRunnable(javaClass);
            activate(runnable, monitor, notifier);
            return true;
        }
        return false;
    }

    private void activate(Runnable runnable, MonitorEntity monitorEntity, MessageNotifierEntity notifierEntity)
            throws TaskActivationException {
        String javaClass = runnable.getClass().getCanonicalName();
        List<Class<?>> interfaces = Arrays.asList(
                runnable.getClass().getInterfaces());
        Task task = new Task(runnable);
        boolean supported = false;
        if (interfaces.contains(IMonitor.class)) {
            supported = true;
            if (monitorEntity == null)
                throw new TaskActivationException.MissingDataEntity();
            if (!javaClass.equals(monitorEntity.getJavaClass()))
                throw new TaskActivationException.InconsistentClass();

            IMonitor monitor = (IMonitor) runnable;
            monitor.initMonitor(new MonitorData(monitorEntity));

            ScheduledFuture<?> future = dashboardScheduler.scheduleAtFixedRate(
                    monitor, monitorEntity.getExecRate());
            task.setMonitorScheduledFuture(future);

        }
        if (interfaces.contains(INotifier.class)) {
            supported = true;
            if (notifierEntity == null)
                throw new TaskActivationException.MissingDataEntity();
            if (!javaClass.equals(notifierEntity.getJavaClass()))
                throw new TaskActivationException.InconsistentClass();

            INotifier notifier = (INotifier) runnable;
            notifier.initNotifier(
                    new NotifierData(notifierEntity),
                    new Messenger(notifierEntity.getId(), messaging));

            if (!notifierEntity.isMonitor()) {
                Thread notifierThread = new Thread(notifier);
                notifierThread.start();
                task.setNotifierThread(notifierThread);
            }
        }
        if (!supported)
            throw new TaskActivationException.NonSupportedTask();
        runningTasks.put(javaClass, task);
    }

    public synchronized void deactivate(String javaClass) {
        Task task = runningTasks.get(javaClass);
        if (task != null) {
            ScheduledFuture<?> monitorScheduledFuture = task.getMonitorScheduledFuture();
            if (monitorScheduledFuture != null)
                monitorScheduledFuture.cancel(true);
            Thread notifierThread = task.getNotifierThread();
            if (notifierThread != null)
                notifierThread.interrupt();
            runningTasks.remove(javaClass);
        }
    }

    public void deactivate(MonitorEntity monitor) {
        if (monitor != null)
            deactivate(monitor.getJavaClass());
    }

    public void deactivate(MessageNotifierEntity notifier) {
        if (notifier != null)
            deactivate(notifier.getJavaClass());
    }

}
