package com.github.command1264.webProgramming.service;

import com.github.command1264.webProgramming.dao.AccountDao;
import com.github.command1264.webProgramming.dao.MessagesDao;
import com.github.command1264.webProgramming.dao.SqlDao;
import com.github.command1264.webProgramming.dao.UsersChatRoomDao;
import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.github.command1264.webProgramming.util.RoomNameConverter;
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
        String usersIdListJsonStr = null;
        if (jsonObject.has("chatRoomName")) {
            chatRoomName = jsonObject.get("chatRoomName").getAsString();
        }

        if (jsonObject.has("users") && chatRoomName == null) {
            usersIdListJsonStr = usersSorter.sortUsersIdList(jsonObject.getAsJsonArray("users"));

            if (usersIdListJsonStr == null) {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage(ErrorType.cantFindChatRoomNameAndUsersRaw.getErrorMessage());
                return returnJsonObject;
            }
            chatRoomName = RoomNameConverter.convertChatRoomName(usersChatRoomDao.getChatRoomUUID(usersIdListJsonStr));
        }
        if (chatRoomName == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindChatRoomNameRaw.getErrorMessage());
            return returnJsonObject;
        }
        return messagesDao.userSendMessage(chatRoomName, jsonObject);
    }

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
