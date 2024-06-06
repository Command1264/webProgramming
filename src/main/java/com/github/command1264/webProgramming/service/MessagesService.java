package com.github.command1264.webProgramming.service;

import com.github.command1264.webProgramming.dao.AccountDao;
import com.github.command1264.webProgramming.dao.MessagesDao;
import com.github.command1264.webProgramming.dao.SqlDao;
import com.github.command1264.webProgramming.dao.UsersChatRoomDao;
import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.MessageSendReceive;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.github.command1264.webProgramming.util.UsersSorter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        if (jsonObject.has("chatRoomName") && !jsonObject.get("chatRoomName").isJsonNull()) {
            chatRoomName = jsonObject.get("chatRoomName").getAsString();
        }
        if (jsonObject.has("message") && !jsonObject.get("message").isJsonNull()) {
            messageSendReceive = MessageSendReceive.deserialize(jsonObject.getAsJsonObject("message").getAsString());
        }

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
        return messagesDao.userSendMessage(chatRoomName, messageSendReceive);
    }

    @Deprecated
    public ReturnJsonObject getUserReceiveMessage(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if(sqlDao.checkNotConnect()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }


        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        try {
            returnJsonObject.setSuccess(true);
        } catch (Exception e) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.exception.getErrorMessage());
            returnJsonObject.setException(e.getMessage());
        }
        return returnJsonObject;
    }
}
