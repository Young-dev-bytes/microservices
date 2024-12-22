package com.in28minutes.microservices.mlagenteval.websockets;

import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketSessionManager {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public void addSession(String key, Session session) {
        sessions.put(key, session);
    }

    public void removeSession(Session session) {
        sessions.values().remove(session);
    }

    public Session getSession(String key) {
        return sessions.get(key);
    }
}
