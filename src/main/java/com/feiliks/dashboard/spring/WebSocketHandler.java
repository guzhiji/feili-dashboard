package com.feiliks.dashboard.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class WebSocketHandler extends TextWebSocketHandler {

    private final Map<WebSocketSession, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Autowired
    private BaseTimeDao baseTimeDao;

    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        WebSocketSession s = new ConcurrentWebSocketSessionDecorator(
                session, 60000, 1024);
        try {
            s.sendMessage(new TextMessage("basetime:" + baseTimeDao.getDBTime().getTime()));
        } catch (IOException ignored) {
        }
        sessions.put(session, s);
    }

    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    public void broadcast(String msg) {
        TextMessage m = new TextMessage(msg);
        for (WebSocketSession session : sessions.values()) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(m);
                } catch (IOException ignored) {
                }
            }
        }
    }

}
