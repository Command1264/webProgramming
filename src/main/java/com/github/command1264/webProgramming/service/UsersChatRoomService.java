package com.github.command1264.webProgramming.service;

import com.github.command1264.webProgramming.accouunt.User;
import com.github.command1264.webProgramming.dao.AccountDao;
import com.github.command1264.webProgramming.dao.MessagesDao;
import com.github.command1264.webProgramming.dao.SqlDao;
import com.github.command1264.webProgramming.dao.UsersChatRoomDao;
import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.github.command1264.webProgramming.util.RoomNameConverter;
import com.github.command1264.webProgramming.util.UsersSorter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
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

    public ReturnJsonObject getUsersChatRoom(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if(sqlDao.checkNotConnect()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        JsonArray users = jsonObject.getAsJsonArray("users");
        return usersChatRoomDao.getUsersChatRoom(usersSorter.sortUsersIdList(users));
    }

    public ReturnJsonObject createUsersChatRoom(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if(sqlDao.checkNotConnect()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }


        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        if (!jsonObject.has("users")) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.usersIsZero.getErrorMessage());
            return returnJsonObject;
        }

        JsonArray users = jsonObject.getAsJsonArray("users");

        String usersListStr = usersSorter.sortUsersIdList(users);

        if (usersListStr == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.usersIsZero.getErrorMessage());
            return returnJsonObject;
        }
        List<String> userIds = gson.fromJson(usersListStr, new TypeToken<List<String>>(){}.getType());
//        for(String id : userIds) {
//            accountDao.addChatRooms(id, )
//        }
        return usersChatRoomDao.createUsersChatRoom(usersListStr);
    }

    public ReturnJsonObject getUsersChatRoomChat(String json) {
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
        return messagesDao.getUsersChatRoomChat(chatRoomName);
    }
}
