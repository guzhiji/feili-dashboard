package com.feiliks.dashboard.spring.services;

import com.feiliks.dashboard.spring.TaskActivationException;
import com.feiliks.dashboard.spring.entities.MonitorEntity;
import com.feiliks.dashboard.AbstractMonitor;
import com.feiliks.dashboard.IDbConnManager;
import com.feiliks.dashboard.spring.impl.Messenger;
import com.feiliks.dashboard.spring.impl.MonitorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;


@Service
public class MonitorService {

    @Autowired
    private SimpMessagingTemplate messaging;

    @Autowired
    private IDbConnManager dbConnManager;

    @Autowired
    private ThreadPoolTaskScheduler monitorScheduler;

    private final Map<Long, AbstractMonitor> monitors = new HashMap<>();

    private AbstractMonitor instantiateMonitor(String javaClass)
            throws TaskActivationException.MonitorNotInstantiated {
        try {
            Class<?> cls = Class.forName(javaClass);
            return (AbstractMonitor) cls.newInstance();
        } catch (Exception e) {
            throw new TaskActivationException.MonitorNotInstantiated(e);
        }
    }

    public AbstractMonitor getMonitor(long id) {
        return monitors.get(id);
    }

    public AbstractMonitor getMonitor(MonitorEntity entity) {
        return monitors.get(entity.getId());
    }

    public synchronized void activate(MonitorEntity entity)
            throws TaskActivationException {

        AbstractMonitor monitor;
        if (monitors.containsKey(entity.getId())) {
            monitor = monitors.get(entity.getId());
        } else {
            monitor = instantiateMonitor(entity.getJavaClass());
            monitor.bind(new MonitorInfo(entity), dbConnManager);
            monitor.bind(new Messenger(messaging));
        }

        AbstractMonitor.Task task = monitor.getTask();
        if (monitor.isRepeatable()) {
            ScheduledFuture<?> sf = monitor.getScheduledFuture();
            if (sf == null || sf.isCancelled()) {
                sf = monitorScheduler.scheduleAtFixedRate(
                        task, entity.getExecRate());
                monitor.setScheduledFuture(sf);
                monitors.put(entity.getId(), monitor);
            } else {
                throw new TaskActivationException.TaskAlreadyActivated();
            }
        } else if (!task.isAlive()) {
            task.start();
            monitor.setScheduledFuture(null);
            monitors.put(entity.getId(), monitor);
        } else {
            throw new TaskActivationException.TaskAlreadyActivated();
        }

    }

    public synchronized void deactivate(MonitorEntity entity) {
        if (monitors.containsKey(entity.getId())) {
            AbstractMonitor monitor = monitors.get(entity.getId());
            ScheduledFuture<?> sf = monitor.getScheduledFuture();
            monitors.remove(entity.getId());
            if (sf != null) {
                sf.cancel(true);
            } else if (!monitor.isRepeatable()) {
                try {
                    AbstractMonitor.Task task = monitor.getTask();
                    task.interrupt();
                } catch (TaskActivationException.TaskNotInstantiated ignored) {
                }
            }
        }
    }

}
