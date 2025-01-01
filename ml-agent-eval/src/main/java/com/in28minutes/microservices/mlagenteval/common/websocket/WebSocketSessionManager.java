package com.in28minutes.microservices.mlagenteval.common.websocket;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketSessionManager {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void addSession(String key, WebSocketSession session) {
        sessions.put(key, session);
    }

    public void removeSession(WebSocketSession session) {
        sessions.values().remove(session);
    }

    public WebSocketSession getSession(String key) {
        return sessions.get(key);
    }
}
