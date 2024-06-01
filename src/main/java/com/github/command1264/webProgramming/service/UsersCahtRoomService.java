package com.github.command1264.webProgramming.service;

import com.github.command1264.webProgramming.accouunt.User;
import com.github.command1264.webProgramming.dao.SqlDao;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class UsersCahtRoomService {
    private SqlDao sqlDao;
    private Gson gson;
    public UsersCahtRoomService(Gson gson, SqlDao sqlDao) {
        this.gson = gson;
        this.sqlDao = sqlDao;
    }

    public ReturnJsonObject getUsersChatRoom(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (!sqlDao.checkNotConnect()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("未連接資料庫");
            return returnJsonObject;
        }

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        JsonArray users = jsonObject.getAsJsonArray("users");
        List<String> usersIdList = new ArrayList<>();

        for (JsonElement jsonElement : users.asList()) {
            try {
                User user = sqlDao.getUser(jsonElement.getAsString());
                if (user == null) continue;

//                usersList.add(user);
                usersIdList.add(user.getId());
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        if (usersIdList.isEmpty()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("人數不能為0");
            return returnJsonObject;
        }
        // 排序，讓之後的聊天室更好判斷
//        usersList.sort(Comparator.comparing((User user) -> user.id));
        usersIdList.sort(Comparator.naturalOrder());
        String usersIdListJsonStr = gson.toJson(gson.toJsonTree(usersIdList, new TypeToken<List<String>>() {
        }.getType()).getAsJsonArray(), JsonArray.class);

        return sqlDao.getUsersChatRoom(usersIdListJsonStr);
    }

    public ReturnJsonObject createUsersChatRoom(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (!sqlDao.checkNotConnect()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("未連接資料庫");
            return returnJsonObject;
        }

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        if (!jsonObject.has("users")) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("人數不能為0");
            return returnJsonObject;
        }

        JsonArray users = jsonObject.getAsJsonArray("users");

        String usersIdListJsonStr = sortUsersIdList(users);

        if (usersIdListJsonStr == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("人數不能為0");
            return returnJsonObject;
        }

        return sqlDao.createUsersChatRoom(usersIdListJsonStr);
    }

    private @Nullable String sortUsersIdList(JsonArray users) {
        List<String> usersIdList = new ArrayList<>();

        for (JsonElement jsonElement : users.asList()) {
            try {
                User user = sqlDao.getUser(jsonElement.getAsString());
                if (user == null) continue;
                usersIdList.add(user.getId());
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        if (usersIdList.isEmpty()) {
            return null;
        }
        // 排序，讓之後的聊天室更好判斷
//        usersList.sort(Comparator.comparing((User user) -> user.id));
        usersIdList.sort(Comparator.naturalOrder());
        return gson.toJson(gson.toJsonTree(usersIdList, new TypeToken<List<String>>() {
        }.getType()).getAsJsonArray(), JsonArray.class);
    }

    public ReturnJsonObject getUsersChatRoomChat(String json) {

        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        ;
        if (!sqlDao.checkNotConnect()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("未連接資料庫");
            return returnJsonObject;
        }

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        String chatRoomName = null;
        String usersIdListJsonStr = null;
        if (jsonObject.has("chatRoomName")) {
            chatRoomName = jsonObject.get("chatRoomName").getAsString();
        }

        if (jsonObject.has("users") && chatRoomName == null) {
            usersIdListJsonStr = sortUsersIdList(jsonObject.getAsJsonArray("users"));

            if (usersIdListJsonStr == null) {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage("找不到chatRoomName或是users");
                return returnJsonObject;
            }
            chatRoomName = convertChatRoomName(sqlDao.getChatRoomUUID(usersIdListJsonStr));
        }
        if (chatRoomName == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("找不到chatRoom");
            return returnJsonObject;
        }
        return sqlDao.getUsersChatRoomChat(chatRoomName);
    }

    public ReturnJsonObject userSendMessage(String json) {


        ReturnJsonObject returnJsonObject = new ReturnJsonObject();

        if (!sqlDao.checkNotConnect()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("未連接資料庫");
            return returnJsonObject;
        }

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        String chatRoomName = null;
        String usersIdListJsonStr = null;
        if (jsonObject.has("chatRoomName")) {
            chatRoomName = jsonObject.get("chatRoomName").getAsString();
        }

        if (jsonObject.has("users") && chatRoomName == null) {
            usersIdListJsonStr = sortUsersIdList(jsonObject.getAsJsonArray("users"));

            if (usersIdListJsonStr == null) {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage("找不到chatRoomName或是users");
                return returnJsonObject;
            }
            chatRoomName = convertChatRoomName(sqlDao.getChatRoomUUID(usersIdListJsonStr));
        }
        if (chatRoomName == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("找不到chatRoom");
            return returnJsonObject;
        }
        return sqlDao.userSendMessage(usersIdListJsonStr, chatRoomName, jsonObject);
    }

    public ReturnJsonObject getUserReceiveMessage(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();

        if (!sqlDao.checkNotConnect()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("未連接資料庫");
            return returnJsonObject;
        }

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        try {
            returnJsonObject.setSuccess(true);
        } catch (Exception e) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("例外");
            returnJsonObject.setException(e.getMessage());
        }
        return returnJsonObject;
    }

    private String convertChatRoomName(UUID uuid) {
        if (uuid == null) return null;
        return "room_" + uuid.toString().replaceAll("-", "_");
    }

    private UUID convertChatRoomName(String roomName) {
        if (roomName == null) return null;
        return UUID.fromString(roomName.replaceAll("_", "-").replaceAll("room_", ""));
    }
}
