package com.in28minutes.microservices.mlagenteval.common.websocket;

import com.in28minutes.microservices.mlagenteval.enums.MessageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Slf4j
public class MessageSendUtils {

    public static void sendMessage(WebSocketSession session, String messageType, String messageData){
        try {
            session.sendMessage(new TextMessage(messageData));
        } catch (IOException ioException) {
            log.error("webSocketSession sendMessage error: {}", ioException.getMessage());
        }
    }

    public static void sendText(WebSocketSession session, String message) {
        if(session == null || !session.isOpen()) {
            return;

        }
        synchronized (session) {
            try {
                sendMessage(session, MessageTypeEnum.MESSAGE.getValue(), message);
            } catch (IllegalStateException e) {
                log.error("WebSocket send msg error...connection has been closed.");
            }
        }

    }
}
