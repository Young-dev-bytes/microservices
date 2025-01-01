/*
 *   sonic-agent  Agent of Sonic Cloud Real Machine Platform.
 *   Copyright (C) 2022 SonicCloudOrg
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.in28minutes.microservices.mlagenteval.common.websocket;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.in28minutes.microservices.mlagenteval.common.config.WsEndpointConfigure;
import com.in28minutes.microservices.mlagenteval.dto.InstanceDto;
import com.in28minutes.microservices.mlagenteval.dto.MessageDto;
import com.in28minutes.microservices.mlagenteval.enums.MessageTypeEnum;
import com.in28minutes.microservices.mlagenteval.enums.MessageWsEnum;
import com.in28minutes.microservices.mlagenteval.utils.JsonUtils;
import com.in28minutes.microservices.mlagenteval.utils.spring.SpringBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Objects;

@Component
@Slf4j
@ServerEndpoint(value = "/websockets/agentEval/logs/{key}", configurator = WsEndpointConfigure.class)
public class AgentEvalLogsWSServer {

    private static final String PING = "PING";
    private static final String PONG = "PONG";

    WebSocketSessionManager webSocketSessionManager = SpringBeanUtils.getBean(WebSocketSessionManager.class);

    @OnOpen
    public void onOpen(WebSocketSession session, @PathParam("key") String key) {
        log.info("session:{}, key:{}", session, key);
    }

    @OnClose
    public void onClose(WebSocketSession session) {
        log.info("close and remove session");
        webSocketSessionManager.removeSession(session);
    }

    @OnError
    public void onError(WebSocketSession session, Throwable error) {
        log.error(error.getMessage());
        error.printStackTrace();
        JSONObject errMsg = new JSONObject();
        errMsg.put("msg", "error");
    }

    @OnMessage
    public void onMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            String payload = textMessage.getPayload();
            if (PING.equals(payload)) {
                session.sendMessage(new TextMessage(PONG));
                return;
            }
            try {
                MessageDto messageDto = JsonUtils.parseObject(payload, MessageDto.class);
                if (messageDto.getData() == null) {
                    return;
                }
                log.info(messageDto.toString());
                MessageWsEnum messageType = MessageWsEnum.getByValue(messageDto.getMessageType());
                if (Objects.isNull(messageType)) {
                    log.error("message type not support, sessionId:{}", session.getId());
                    MessageSendUtils.sendMessage(session, MessageTypeEnum.SERVER_ERROR.getValue(), "消息类型目前仅支持查询");
                    return;
                }
                switch (messageType) {
                    case QUERY_EVAL_LOG_MESSAGE:
                        Object data = messageDto.getData();
                        InstanceDto instanceDto = JsonUtils.parseObject(JsonUtils.toJsonString(data), InstanceDto.class);
                        if (!Strings.isNullOrEmpty(instanceDto.getInstanceId())) {
                            webSocketSessionManager.addSession(instanceDto.getInstanceId(), session);
                        }
                        break;
                    default:
                        log.error("message type not support, sessionId:{}", session.getId());
                        MessageSendUtils.sendMessage(session, MessageTypeEnum.SERVER_ERROR.getValue(), "消息类型目前仅支持查询");
                        break;
                }
            } catch (Exception exception) {
                log.error("handle message fail, sessionId:{}", session.getId());
                log.error(exception.getMessage(), exception);
                MessageSendUtils.sendMessage(session, MessageTypeEnum.SERVER_ERROR.getValue(), "消息处理失败");
            }
        }
    }
}
