package com.in28minutes.microservices.mlagenteval.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.io.IOException;

@Slf4j
@Service
public class AgentOperationService {

    public void sendText(Session session, String message) {
        if (session == null || !session.isOpen()) {
            return;
        }
        synchronized (session) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IllegalStateException | IOException e) {
                log.error("WebSocket send msg error...connection has been closed.");
            }
        }
    }
}
