package com.github.command1264.webProgramming.service;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class UsersChatRoomService {
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
    private Gson gson = new Gson();

    @Deprecated
    public ReturnJsonObject getUsersChatRoom(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if(sqlDao.checkNotConnect()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        if ((!jsonObject.has(JsonKeyEnum.userIds.name()) || jsonObject.get(JsonKeyEnum.userIds.name()).isJsonNull()) &&
                (!jsonObject.has(JsonKeyEnum.ids.name()) || jsonObject.get(JsonKeyEnum.ids.name()).isJsonNull()) ) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.usersIsZero.getErrorMessage());
            return returnJsonObject;
        }


        String usersListStr = null;
        if (jsonObject.has(JsonKeyEnum.userIds.name()) && !jsonObject.get(JsonKeyEnum.userIds.name()).isJsonNull()) {
            usersListStr = usersSorter.sortUserIdsReturnIds(jsonObject.getAsJsonArray(JsonKeyEnum.userIds.name()));
        } else if (jsonObject.has(JsonKeyEnum.ids.name()) && !jsonObject.get(JsonKeyEnum.ids.name()).isJsonNull()) {
            usersListStr = usersSorter.sortIds(jsonObject.getAsJsonArray(JsonKeyEnum.ids.name()));
        }

        return usersChatRoomDao.getUsersChatRoom(usersListStr);
    }

    public ReturnJsonObject createUsersChatRoom(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if(sqlDao.checkNotConnect()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }


        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        if ((!jsonObject.has(JsonKeyEnum.userIds.name()) || jsonObject.get(JsonKeyEnum.userIds.name()).isJsonNull()) &&
                (!jsonObject.has(JsonKeyEnum.ids.name()) || jsonObject.get(JsonKeyEnum.ids.name()).isJsonNull()) ) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.usersIsZero.getErrorMessage());
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


        String usersListStr = null;

        if (jsonObject.has(JsonKeyEnum.userIds.name()) && !jsonObject.get(JsonKeyEnum.userIds.name()).isJsonNull()) {
            usersListStr = usersSorter.sortUserIdsReturnIds(jsonObject.getAsJsonArray(JsonKeyEnum.userIds.name()));
        } else if (jsonObject.has(JsonKeyEnum.ids.name()) && !jsonObject.get(JsonKeyEnum.ids.name()).isJsonNull()) {
            usersListStr = usersSorter.sortIds(jsonObject.getAsJsonArray(JsonKeyEnum.ids.name()));
        }

        if (usersListStr == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.usersIsZero.getErrorMessage());
            return returnJsonObject;
        }
        List<String> userIds = gson.fromJson(usersListStr, new TypeToken<List<String>>(){}.getType());
        String tokenId = accountDao.getIdWithToken(token);
        if (!userIds.contains(tokenId)) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.tokenNoPermission.getErrorMessage());
            return returnJsonObject;
        }

        ReturnJsonObject createRoomJson = usersChatRoomDao.createUsersChatRoom(usersListStr);
        if (!createRoomJson.isSuccess()) return createRoomJson;
        boolean flag = false;
        for(String id : userIds) {
            if (!accountDao.addChatRoomsWithId(id, createRoomJson.getData())) {
                flag = true;
            }
        }

        if (flag) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantSuccessAddAccount.getErrorMessage());
            return returnJsonObject;
        }
        return createRoomJson;

    }

    public ReturnJsonObject getUsersChatRoomChats(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if(sqlDao.checkNotConnect()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }


        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        if ((!jsonObject.has(JsonKeyEnum.chatRoomName.name()) || jsonObject.get(JsonKeyEnum.chatRoomName.name()).isJsonNull())) {
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

        List<String> chatRoomNames = new ArrayList<>();
        try {
            chatRoomNames.add(jsonObject.get(JsonKeyEnum.chatRoomName.name()).getAsString());
        } catch (Exception e) {
            try {
                jsonObject.get(JsonKeyEnum.chatRoomName.name()).getAsJsonArray().asList().forEach((jsonElement -> {
                    String str = jsonElement.getAsString();
                    if (str != null) chatRoomNames.add(str.toLowerCase());
                }));
            } catch (Exception e2) {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage(ErrorType.cantFindChatRoomName.getErrorMessage());
                return returnJsonObject;
            }
        }

        if (chatRoomNames.isEmpty()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindChatRoomName.getErrorMessage());
            return returnJsonObject;
        }

        Map<String, List<MessageSendReceive>> chatRoomsChats = new HashMap<>();
//        chatRoomNames = chatRoomName.toLowerCase();
        for(String chatRoomName : chatRoomNames) {
            if (!usersChatRoomDao.checkHasUsersChatRoom(chatRoomName) ||
                    !sqlDao.findTableName(chatRoomName)) {
                continue;
//                returnJsonObject.setSuccess(false);
//                returnJsonObject.setErrorMessage(ErrorType.cantFindChatRoomName.getErrorMessage());
//                return returnJsonObject;
            }
            if (!usersChatRoomDao.getUsersChatRoomUsers(chatRoomName).contains(tokenId)) continue;

            chatRoomsChats.put(chatRoomName, messagesDao.getUsersChatRoomChat(token, chatRoomName));
        }
        returnJsonObject.setSuccess(true);
        returnJsonObject.setErrorMessage("");
        returnJsonObject.setException("");
        returnJsonObject.setData(gson.toJson(chatRoomsChats,
                new TypeToken<Map<String, List<MessageSendReceive>>>(){}.getType()));
        return returnJsonObject;
    }
}
