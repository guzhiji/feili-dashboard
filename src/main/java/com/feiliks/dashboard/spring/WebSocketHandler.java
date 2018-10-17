package com.feiliks.dashboard.spring;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class WebSocketHandler extends TextWebSocketHandler {
    private List<WebSocketSession> sessions = Collections.synchronizedList(new ArrayList<>());

    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    public void broadcast(String msg) {
        TextMessage m = new TextMessage(msg);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(m);
                } catch (IOException ignored) {
                }
            }
        }
    }

}
