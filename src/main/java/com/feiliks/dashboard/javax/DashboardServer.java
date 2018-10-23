package com.feiliks.dashboard.javax;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/websocket")
public class DashboardServer {

    public static abstract class DashboardMonitorTask implements Runnable {

        public void broadcast(String type, String msg) {
            QUEUE.add(type + ":" + System.currentTimeMillis() + ":" + msg);
        }

    }

    private final static Set<Session> SESSIONS = new CopyOnWriteArraySet<>();
    private final static ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(4);
    private final static ConcurrentLinkedQueue<String> QUEUE = new ConcurrentLinkedQueue<>();
    private static boolean running = true;

    static {
        SCHEDULER.scheduleAtFixedRate(new MemoryMonitor(), 0, 1, TimeUnit.SECONDS);
        SCHEDULER.scheduleAtFixedRate(new CpuMonitor(), 0, 2, TimeUnit.SECONDS);
        SCHEDULER.scheduleAtFixedRate(new NetMonitor(), 0, 2, TimeUnit.SECONDS);
        SCHEDULER.scheduleAtFixedRate(new DiskMonitor(), 0, 2, TimeUnit.SECONDS);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    while (true) {
                        String msg = QUEUE.poll();
                        if (msg != null) {
                            for (Session session : SESSIONS) {
                                try {
                                    session.getBasicRemote().sendText(msg);
                                } catch (IOException ex) {
                                }
                            }
                        } else {
                            break;
                        }
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        }).start();
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
