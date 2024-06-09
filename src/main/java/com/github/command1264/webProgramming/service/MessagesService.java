package com.github.command1264.webProgramming.service;

import com.github.command1264.webProgramming.accouunt.UserAndRooms;
import com.github.command1264.webProgramming.dao.AccountDao;
import com.github.command1264.webProgramming.dao.MessagesDao;
import com.github.command1264.webProgramming.dao.SqlDao;
import com.github.command1264.webProgramming.dao.UsersChatRoomDao;
import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.MessageSendReceive;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.github.command1264.webProgramming.util.JsonKeyEnum;
import com.github.command1264.webProgramming.util.UsersSorter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class MessagesService {
    private final Gson gson = new Gson();
    @Autowired
    private UsersSorter usersSorter;
    @Autowired
    private SqlDao sqlDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private UsersChatRoomDao usersChatRoomDao;
    @Autowired
    private MessagesDao messagesDao;

    public ReturnJsonObject userSendMessage(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if(sqlDao.checkNotConnect()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        String chatRoomName = null;
        MessageSendReceive messageSendReceive = null;
        if (jsonObject.has(JsonKeyEnum.chatRoomName.name()) && !jsonObject.get(JsonKeyEnum.chatRoomName.name()).isJsonNull()) {
            chatRoomName = jsonObject.get(JsonKeyEnum.chatRoomName.name()).getAsString();
        }
        if (jsonObject.has(JsonKeyEnum.message.name()) && !jsonObject.get(JsonKeyEnum.message.name()).isJsonNull()) {
            messageSendReceive = MessageSendReceive.deserialize(jsonObject.getAsJsonObject(JsonKeyEnum.message.name()));
        }
        if ((!jsonObject.has(JsonKeyEnum.token.name()) || jsonObject.get(JsonKeyEnum.token.name()).isJsonNull())) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindToken.getErrorMessage());
            return returnJsonObject;
        }
        String token = null;
        try {
            token = jsonObject.get(JsonKeyEnum.token.name()).getAsString();
        } catch (Exception e) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindToken.getErrorMessage());
            return returnJsonObject;
        }
        String tokenId = accountDao.getIdWithToken(token);

        if (chatRoomName == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindChatRoomName.getErrorMessage());
            return returnJsonObject;
        }
        if (messageSendReceive == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindMessage.getErrorMessage());
            return returnJsonObject;
        }

        UserAndRooms userAndRooms = accountDao.getUserAndRoomsWithUserId(messageSendReceive.getSender());
        if (userAndRooms != null) {
            List<String> chatRoomUserList = usersChatRoomDao.getUsersChatRoomUsers(chatRoomName);
            if (!Objects.equals(tokenId, userAndRooms.getId()) ||
                !chatRoomUserList.contains(tokenId)) {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage(ErrorType.tokenNoPermission.getErrorMessage());
                return returnJsonObject;
            }
            messageSendReceive.setSender(userAndRooms.getId());
        } else {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindUsers.getErrorMessage());
            return returnJsonObject;
        }
        return messagesDao.userSendMessage(chatRoomName, messageSendReceive);
    }

    public ReturnJsonObject getUserReceiveMessage(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if(sqlDao.checkNotConnect()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        if (!jsonObject.has(JsonKeyEnum.chatRoomName.name()) || jsonObject.get(JsonKeyEnum.chatRoomName.name()).isJsonNull()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindChatRoomName.getErrorMessage());
            return returnJsonObject;
        }
        if ((!jsonObject.has(JsonKeyEnum.token.name()) || jsonObject.get(JsonKeyEnum.token.name()).isJsonNull())) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindToken.getErrorMessage());
            return returnJsonObject;
        }
        String token = null;
        try {
            token = jsonObject.get(JsonKeyEnum.token.name()).getAsString();
        } catch (Exception e) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindToken.getErrorMessage());
            return returnJsonObject;
        }
        String tokenId = accountDao.getIdWithToken(token);

        JsonObject chatRooms = null;
        try {
            chatRooms = jsonObject.getAsJsonObject(JsonKeyEnum.chatRoomName.name());
        } catch (Exception e) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindChatRoomName.getErrorMessage());
            return returnJsonObject;
        }

        if (chatRooms == null || chatRooms.isEmpty()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindChatRoomName.getErrorMessage());
            return returnJsonObject;
        }

        JsonObject dataJsonObject = new JsonObject();
        chatRooms.asMap().forEach((chatRoomName, id) -> {
            List<String> userList = usersChatRoomDao.getUsersChatRoomUsers(chatRoomName);
            if (userList.contains(tokenId)) {
                String idStr = null;
                try {
                    idStr = id.getAsString();
                } catch (Exception ignored) {}
                if (idStr != null) {
                    List<MessageSendReceive> messageSendReceiveList = messagesDao.userReceiveMessageWithId(chatRoomName, idStr);
                    dataJsonObject.addProperty(chatRoomName, gson.toJson(messageSendReceiveList, new TypeToken<List<MessageSendReceive>>() {
                    }.getType()));
                }
            }
        });
        if (dataJsonObject.isEmpty() && !chatRooms.isEmpty()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.tokenNoPermission.getErrorMessage());
            return returnJsonObject;
        }
        returnJsonObject.setSuccess(true);
        returnJsonObject.setData(gson.toJson(dataJsonObject, JsonObject.class));
        return returnJsonObject;
    }
}
