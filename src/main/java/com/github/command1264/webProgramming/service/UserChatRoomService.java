package com.github.command1264.webProgramming.service;

import com.github.command1264.webProgramming.dao.AccountDao;
import com.github.command1264.webProgramming.dao.MessagesDao;
import com.github.command1264.webProgramming.dao.SqlDao;
import com.github.command1264.webProgramming.dao.UserChatRoomDao;
import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.github.command1264.webProgramming.userChatRoom.UserChatRoom;
import com.github.command1264.webProgramming.util.JsonKeyEnum;
import com.github.command1264.webProgramming.util.RoomNameConverter;
import com.github.command1264.webProgramming.util.UsersSorter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class UserChatRoomService {
    @Autowired
    private UsersSorter usersSorter;
    @Autowired
    private SqlDao sqlDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private UserChatRoomDao userChatRoomDao;
    @Autowired
    private MessagesDao messagesDao;
    private Gson gson = new Gson();

    public ReturnJsonObject getUserChatRoom(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
//        if(sqlDao.checkNotConnect()) {
//            returnJsonObject.setSuccess(false);
//            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
//            return returnJsonObject;
//        }

        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (Exception e) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getErrorMessage());
            return returnJsonObject;
        }
        if (!jsonObject.has(JsonKeyEnum.chatRoomName.name()) || jsonObject.get(JsonKeyEnum.chatRoomName.name()).isJsonNull()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindChatRoom.getErrorMessage());
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

        List<UUID> chatRoomUUIDs = new ArrayList<>();
        try {
            UUID chatRoomUUID;
            try {
                chatRoomUUID = UUID.fromString(jsonObject.get(JsonKeyEnum.chatRoomName.name()).getAsString());
            } catch (Exception e) {
                chatRoomUUID = RoomNameConverter.convertChatRoomName(jsonObject.get(JsonKeyEnum.chatRoomName.name()).getAsString());
            }
            chatRoomUUIDs.add(chatRoomUUID);

        } catch (Exception e) {
            try {
                jsonObject.get(JsonKeyEnum.chatRoomName.name()).getAsJsonArray().asList().forEach((jsonElement -> {
                    String chatRoomName = null;
                    try {
                        chatRoomName = jsonElement.getAsString();
                    } catch (Exception ignored) {}
                    if (chatRoomName != null) {
                        UUID chatRoomUUID;
                        try {
                            chatRoomUUID = UUID.fromString(chatRoomName);
                        } catch (Exception ex) {
                            chatRoomUUID = RoomNameConverter.convertChatRoomName(chatRoomName);
                        }
                        chatRoomUUIDs.add(chatRoomUUID);
                    }
                }));
            } catch (Exception ignored) {}
        }
        if (chatRoomUUIDs.isEmpty()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindChatRoomName.getErrorMessage());
            return returnJsonObject;
        }

        Map<String, Object> dataMap = new HashMap<>();
        for (UUID uuid : chatRoomUUIDs) {
            UserChatRoom userChatRoom = userChatRoomDao.getUserChatRoom(uuid);
            if (userChatRoom != null) {
                if (userChatRoom.getUserList().contains(tokenId)) {
                    dataMap.put(RoomNameConverter.convertChatRoomName(uuid),
                            userChatRoom);
                }
            }
        }
        if (dataMap.isEmpty()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.tokenNoPermission.getErrorMessage());
            return returnJsonObject;
        }
        returnJsonObject.setSuccessAndData(dataMap);
        return returnJsonObject;
    }

    public ReturnJsonObject createUserChatRoom(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
//        if(sqlDao.checkNotConnect()) {
//            returnJsonObject.setSuccess(false);
//            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
//            return returnJsonObject;
//        }


        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (Exception e) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getErrorMessage());
            return returnJsonObject;
        }
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
        String name = null;

        if (jsonObject.has(JsonKeyEnum.name.name()) && !jsonObject.get(JsonKeyEnum.name.name()).isJsonNull()) {
            try {
                name = jsonObject.get(JsonKeyEnum.name.name()).getAsString();
            } catch (Exception ignored) {}
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

        ReturnJsonObject createRoomJson = userChatRoomDao.createUserChatRoom(usersListStr, name);
        if (!createRoomJson.isSuccess()) return createRoomJson;
        boolean flag = false;
        for(String id : userIds) {
            if (!accountDao.addChatRoomsWithId(id, String.valueOf(createRoomJson.getData()))) {
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

}
