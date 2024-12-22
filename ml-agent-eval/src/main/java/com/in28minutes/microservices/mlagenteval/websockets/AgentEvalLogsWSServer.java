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
package com.in28minutes.microservices.mlagenteval.websockets;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.in28minutes.microservices.mlagenteval.common.config.WsEndpointConfigure;
import com.in28minutes.microservices.mlagenteval.utils.spring.SpringBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@Component
@Slf4j
@ServerEndpoint(value = "/websockets/agentEval/logs/{instanceId}", configurator = WsEndpointConfigure.class)
public class AgentEvalLogsWSServer {

    WebSocketSessionManager webSocketSessionManager = SpringBeanUtils.getBean(WebSocketSessionManager.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("instanceId") String instanceId) {
        log.info("session:{}, instanceId:{}", session, instanceId);
        webSocketSessionManager.addSession(instanceId, session);
    }

    @OnClose
    public void onClose(Session session) {
        log.info("close and remove session");
        webSocketSessionManager.removeSession(session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error(error.getMessage());
        error.printStackTrace();
        JSONObject errMsg = new JSONObject();
        errMsg.put("msg", "error");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("message:{}", message);
        JSONObject msg = JSON.parseObject(message);
        log.info("msg:{}", msg);
        // Send a message to the client
    }
}
