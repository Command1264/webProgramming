package com.github.command1264.webProgramming.webSocket;

import com.github.command1264.webProgramming.service.MessagesService;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ServerEndpoint(value = "/channel/room/{roomName}")
public class WebSocket {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocket.class);
    public static final Map<String, List<Session>> webSocketMap = new HashMap<>();

    private Session session;
    private String roomName = "";

    @Autowired
    private MessagesService messagesService;

    @OnMessage
    public void onMessage(String message) throws IOException {

        LOGGER.info("[websocket] 收到消息：id={}，message={}，roomName={}", this.session.getId(), message, roomName);

        if (message.equalsIgnoreCase("bye")) {
            // 由服务器主动关闭连接。状态码为 NORMAL_CLOSURE（正常关闭）。
            this.session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Bye"));;
            return;
        }

        this.session.getAsyncRemote().sendText("["+ roomName +"] Hello " + message);
    }
//    @OnMessage
//    public void onMessage(String json) {
//        messagesService.userSendMessage(json);
//    }

    // 连接打开
    @OnOpen
    public void onOpen(Session session, @PathParam("roomName") String roomName, EndpointConfig endpointConfig){
        // 保存 session 到对象
        this.session = session;
        this.roomName = roomName;
    }

    // 连接关闭
    @OnClose
    public void onClose(CloseReason closeReason){
        LOGGER.info("[websocket] 连接断开：id={}，reason={}", this.session.getId(), closeReason);
        this.deleteSessionMapData();
    }

    // 连接异常
    @OnError
    public void onError(Throwable throwable) throws IOException {

        LOGGER.info("[websocket] 连接异常：id={}，throwable={}", this.session.getId(), throwable.getMessage());
        this.deleteSessionMapData();
        // 关闭连接。状态码为 UNEXPECTED_CONDITION（意料之外的异常）
        this.session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, throwable.getMessage()));
    }

    private void addSessionMapData() {
        List<Session> sessionList = webSocketMap.getOrDefault(roomName, new ArrayList<>());
        sessionList.add(session);
        webSocketMap.put(roomName, sessionList);
        LOGGER.info("[websocket] 新的连接：id={}", this.session.getId());
    }

    private void deleteSessionMapData() {
        List<Session> sessionList = webSocketMap.getOrDefault(roomName, new ArrayList<>());
        if (sessionList.isEmpty()) return;
        sessionList.remove(this.session);
        webSocketMap.put(roomName, sessionList);
    }
}
