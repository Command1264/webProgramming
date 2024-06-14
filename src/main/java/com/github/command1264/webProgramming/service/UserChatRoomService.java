package com.github.command1264.webProgramming.service;

import com.github.command1264.webProgramming.dao.AccountDao;
import com.github.command1264.webProgramming.dao.MessagesDao;
import com.github.command1264.webProgramming.dao.SqlDao;
import com.github.command1264.webProgramming.dao.UserChatRoomDao;
import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.github.command1264.webProgramming.userChatRoom.UserChatRoom;
import com.github.command1264.webProgramming.util.JsonChecker;
import com.github.command1264.webProgramming.util.JsonKeyEnum;
import com.github.command1264.webProgramming.util.RoomNameConverter;
import com.github.command1264.webProgramming.util.UsersSorter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


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
    private final Gson gson = new Gson();

    public ReturnJsonObject getUserChatRoom(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();

        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getErrorMessage());
        }
        if (!jsonObject.has(JsonKeyEnum.chatRoomName.name()) || jsonObject.get(JsonKeyEnum.chatRoomName.name()).isJsonNull()) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindChatRoom.getErrorMessage());
        }

        if ((!jsonObject.has(JsonKeyEnum.token.name()) || jsonObject.get(JsonKeyEnum.token.name()).isJsonNull())) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindToken.getErrorMessage());
        }
        String token = null;
        try {
            token = jsonObject.get(JsonKeyEnum.token.name()).getAsString();
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindToken.getErrorMessage());
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
                    } catch (Exception ignored) {
                    }
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
            } catch (Exception ignored) {
            }
        }
        if (chatRoomUUIDs.isEmpty()) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindChatRoomName.getErrorMessage());
        }

        Map<String, UserChatRoom> dataMap = new HashMap<>();
        for (UUID uuid : chatRoomUUIDs) {
            UserChatRoom userChatRoom = userChatRoomDao.getUserChatRoom(uuid);
            if (userChatRoom != null) {
                if (userChatRoom.getUserList().contains(tokenId)) {
                    dataMap.put(
                            RoomNameConverter.convertChatRoomName(uuid),
                            userChatRoom
                    );
                }
            }
        }
        if (dataMap.isEmpty()) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.tokenNoPermission.getErrorMessage());
        }
        // 進行對於時間的排序(由最晚的時間至最早的時間)
        dataMap = dataMap.entrySet()
                        .stream()
                        .sorted((Map.Entry<String, UserChatRoom> e1, Map.Entry<String, UserChatRoom> e2) ->
                            e2.getValue().getLastModifyWithTime().compareTo(e1.getValue().getLastModifyWithTime()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue,
                                (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        returnJsonObject.setSuccessAndData(dataMap);
        return returnJsonObject;
    }

    // todo need test
    public ReturnJsonObject createUserChatRoom(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();

        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getErrorMessage());
        }

        if (!JsonChecker.checkKey(jsonObject, JsonKeyEnum.userIds.name())) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.usersIsZero.getErrorMessage());
        }
        if (!JsonChecker.checkKey(jsonObject, JsonKeyEnum.token.name())) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindToken.getErrorMessage());
        }
        String token = null;
        try {
            token = jsonObject.get(JsonKeyEnum.token.name()).getAsString();
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindToken.getErrorMessage());
        }


        String usersListStr;
        try {
            usersListStr = usersSorter.sortUserIdsReturnIds(jsonObject.getAsJsonArray(JsonKeyEnum.userIds.name()));
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.usersIsZero.getErrorMessage());
        }
        String name = null;

        if (JsonChecker.checkKey(jsonObject, JsonKeyEnum.name.name())) {
            try {
                name = jsonObject.get(JsonKeyEnum.name.name()).getAsString();
            } catch (Exception ignored) {
            }
        }

        if (usersListStr == null) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.usersIsZero.getErrorMessage());
        }
        List<String> userIds = gson.fromJson(usersListStr, new TypeToken<List<String>>(){}.getType());
        String tokenId = accountDao.getIdWithToken(token);
        if (!userIds.contains(tokenId)) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.tokenNoPermission.getErrorMessage());
        }

        String chatRoomName = userChatRoomDao.createUserChatRoom(usersListStr, name);
        if (chatRoomName == null)
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.sqlUpdateFailed.getErrorMessage());

        boolean flag = false;
        for (String id : userIds) {
            if (!accountDao.addChatRoomsWithId(id, chatRoomName)) {
                flag = true;
            }
        }
        if (flag) return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantSuccessAddAccount.getErrorMessage());

        for (int i = 0; i < 10 && !messagesDao.systemSendMessage(chatRoomName, "create ChatRoom", "create"); i++) {}

        return returnJsonObject.setSuccessAndData(chatRoomName);
    }

}
