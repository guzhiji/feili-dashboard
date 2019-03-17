package com.feiliks.dashboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feiliks.dashboard.spring.TaskActivationException;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;


public abstract class AbstractMonitor {

    private IMonitorInfo monitor;
    private IMessenger messenger;
    private DataSource dataSource;

    private Task task;
    private ScheduledFuture<?> scheduledFuture;
    private final Class<? extends AbstractMonitor> monitorClass;
    private final Class<? extends Task> taskClass;
    private final boolean repeatable;
    private final Map<String, String> resultStore = new ConcurrentHashMap<>();
    private final Map<String, String> resultSources = new HashMap<>();
    private final Map<String, String> messageSources = new HashMap<>();

    public abstract class Task extends Thread {

        public final IMonitorInfo getMonitorInfo() {
            return monitor;
        }

        public final DataSource getDataSource() {
            return dataSource;
        }

        public final void sendMessage(String messageSource, String message) {
            if (messenger != null && messageSources.containsKey(messageSource))
                messenger.send(
                        monitor.getId(),
                        messageSource,
                        message);
        }

        public final <T> void sendMessage(String messageSource, NotifierMessage<T> message) {
            if (messenger != null && messageSources.containsKey(messageSource)) {
                try {
                    messenger.send(
                            monitor.getId(),
                            messageSource,
                            new ObjectMapper().writeValueAsString(message));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }

        public final void exportPreformattedResult(String resultSource, String result) {
            if (resultSources.containsKey(resultSource))
                resultStore.put(resultSource, result);
        }

        public final void exportResult(String resultSource, Object result) {
            if (resultSources.containsKey(resultSource)) {
                try {
                    resultStore.put(resultSource,
                            new ObjectMapper().writeValueAsString(result));
                } catch (JsonProcessingException e) {
                    resultStore.put(resultSource, null);
                    e.printStackTrace();
                }
            }
        }

    }

    public AbstractMonitor(
            Class<? extends AbstractMonitor> monitorClass,
            Class<? extends Task> taskClass,
            boolean repeatable) {
        this.monitorClass = monitorClass;
        this.taskClass = taskClass;
        this.repeatable = repeatable;
    }

    protected final void registerResultSource(String name, String type) {
        resultSources.put(name, type);
    }

    protected final void registerMessageSource(String name, String type) {
        messageSources.put(name, type);
    }

    public final Map<String, String> getResultSources() {
        return resultSources;
    }

    public final Map<String, String> getMessageSources() {
        return messageSources;
    }

    public final boolean isRepeatable() {
        return repeatable;
    }

    public final void setScheduledFuture(ScheduledFuture<?> sf) {
        scheduledFuture = sf;
    }

    public final ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }

    public final Task getTask() throws TaskActivationException.TaskNotInstantiated {
        try {
            if (task == null) {
                Constructor<? extends Task> cst = taskClass.getConstructor(monitorClass);
                task = cst.newInstance(this);
            }
            return task;
        } catch (Exception e) {
            throw new TaskActivationException.TaskNotInstantiated(e);
        }
    }

    public final void bind(IMonitorInfo monitor, IDbConnManager dbConnManager) {
        this.monitor = monitor;
        if (dbConnManager != null && monitor.getDatabaseInfo() != null)
            this.dataSource = dbConnManager.getDatabase(
                    monitor.getDatabaseInfo());
        else
            this.dataSource = null;
    }

    public final void bind(IMessenger messenger) {
        this.messenger = messenger;
    }

    public final String retrieveResult(String resultSource) {
        String result = resultStore.get(resultSource);
        if (result == null) return "null";
        return result;
    }

}
