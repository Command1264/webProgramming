package com.github.command1264.webProgramming.service;

import com.github.command1264.webProgramming.dao.AccountDao;
import com.github.command1264.webProgramming.dao.MessagesDao;
import com.github.command1264.webProgramming.dao.SqlDao;
import com.github.command1264.webProgramming.dao.UsersChatRoomDao;
import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.github.command1264.webProgramming.util.JsonKeyEnum;
import com.github.command1264.webProgramming.util.UsersSorter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


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

        ReturnJsonObject createRoomJson = usersChatRoomDao.createUsersChatRoom(usersListStr);
        if (!createRoomJson.isSuccess()) return createRoomJson;
        boolean flag = false;
        for(String id : userIds) {
//            if (Objects.equals(type, JsonKeyEnum.ids.name())) {
            if (!accountDao.addChatRoomsWithId(id, createRoomJson.getData())) {
                flag = true;
            }
//            } else {
//                if (!accountDao.addChatRoomsWithUserId(id, createRoomJson.getData())) {
//                    flag = true;
//                }
//            }
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
        String chatRoomName = null;
        if (jsonObject.has(JsonKeyEnum.chatRoomName.name())) {
            chatRoomName = jsonObject.get(JsonKeyEnum.chatRoomName.name()).getAsString();
        }

        if (chatRoomName == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindChatRoomName.getErrorMessage());
            return returnJsonObject;
        }
        chatRoomName = chatRoomName.toLowerCase();
        if (!usersChatRoomDao.checkHasUsersChatRoom(chatRoomName) ||
                !sqlDao.findTableName(chatRoomName)) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindChatRoomName.getErrorMessage());
            return returnJsonObject;
        }
        return messagesDao.getUsersChatRoomChat(chatRoomName);
    }
}
