package com.feiliks.dashboard.javax;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ServerEndpoint("/websocket")
public class DashboardServer {

    private final static ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(4);
    final static Set<Session> SESSIONS = new CopyOnWriteArraySet<>();
    static boolean running = true;

    static {
        SCHEDULER.scheduleAtFixedRate(new MemoryMonitor(), 0, 1, TimeUnit.SECONDS);
        SCHEDULER.scheduleAtFixedRate(new CpuMonitor(), 0, 2, TimeUnit.SECONDS);
        SCHEDULER.scheduleAtFixedRate(new NetMonitor(), 0, 2, TimeUnit.SECONDS);
        SCHEDULER.scheduleAtFixedRate(new DiskMonitor(), 0, 2, TimeUnit.SECONDS);
        new WebSocketMessenger().start();
    }

    public static void shutdown() {
        running = false;
        SCHEDULER.shutdown();
    }

    @OnOpen
    public void onOpen(Session session) {
        SESSIONS.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        SESSIONS.remove(session);
    }
}
