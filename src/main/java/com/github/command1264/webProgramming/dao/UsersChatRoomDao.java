package com.github.command1264.webProgramming.dao;

import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.MessageSendReceive;
import com.github.command1264.webProgramming.messages.MessageSendReceiveRowMapper;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.github.command1264.webProgramming.userChatRoom.UsersChatRoom;
import com.github.command1264.webProgramming.userChatRoom.UsersChatRoomRowMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UsersChatRoomDao {
    private Gson gson = new Gson();
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private SqlDao sqlDao;


    public UUID getChatRoomUUID(String users) {
        if (jdbcTemplate == null) return null;
        String sql = "select * from userchatrooms where users=:users;";
        Map<String, Object> map = new HashMap<>() {{
            put("users", users);
        }};
        List<UsersChatRoom> result = jdbcTemplate.query(sql, map, new UsersChatRoomRowMapper());
        if (result.size() != 1) return null;
        return result.get(0).getUUID();
    }

    public ReturnJsonObject getUsersChatRoom(String usersIdListJsonStr) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (jdbcTemplate == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }
        UUID chatUUID = getChatRoomUUID(usersIdListJsonStr);
        if (chatUUID == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindChatRoom.getErrorMessage());
            return returnJsonObject;
        }
        returnJsonObject.setSuccess(true);
        returnJsonObject.setData(chatUUID.toString());
        return returnJsonObject;
    }

    public ReturnJsonObject createUsersChatRoom(String usersIdListJsonStr) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (jdbcTemplate == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }

        if (sqlDao.checkRepeat("userchatrooms", "users", usersIdListJsonStr)) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.chatRoomExist.getErrorMessage());
            return returnJsonObject;
        }
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (sqlDao.checkRepeat("userchatrooms", "uuid", uuid.toString()));
        String roomName = convertChatRoomName(uuid);

        String insertSql = "insert into userChatRooms (uuid, users)  values(:uuid, :users) );";
        UUID finalUuid = uuid;
        Map<String, Object> insertMap = new HashMap<>() {{
            put("uuid", finalUuid.toString());
            put("users", usersIdListJsonStr);
        }};
        int insertRoomCount = jdbcTemplate.update(insertSql, insertMap);

        String createSql = """
            create table :tableName(
                id int not null primary key auto_increment,
                sender varchar(64) not null,
                message text not null,
                type varchar(20) not null default 'text',
                time datetime(4) not null
            );
        """.replaceAll(":tableName", roomName);

        jdbcTemplate.update(createSql, new HashMap<>());

        if (insertRoomCount != 1 || sqlDao.findTableName(roomName)) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlUpdateFailed.getErrorMessage());
            return returnJsonObject;
        } else {
            returnJsonObject.setSuccess(true);
        }
        return returnJsonObject;
    }

    public ReturnJsonObject getUsersChatRoomChat(String chatRoomName) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (jdbcTemplate == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }
        if (chatRoomName == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindChatRoomName.getErrorMessage());
            return returnJsonObject;
        }
        String sql = "select * from :tableName;".replaceAll(":tableName", chatRoomName);
        List<MessageSendReceive> messagesList = jdbcTemplate.query(sql, new HashMap<>(), new MessageSendReceiveRowMapper());

        String messageListStr = gson.toJson(gson.toJsonTree(messagesList,
                        new TypeToken<List<MessageSendReceive>>() {
                        }.getType())
                .getAsJsonArray(), JsonArray.class);
        returnJsonObject.setSuccess(true);
        returnJsonObject.setData(messageListStr);
        return returnJsonObject;
    }


    public String convertChatRoomName(UUID uuid) {
        if (uuid == null) return null;
        return "room_" + uuid.toString().replaceAll("-", "_");
    }

    public UUID convertChatRoomName(String roomName) {
        if (roomName == null) return null;
        return UUID.fromString(roomName.replaceAll("_", "-").replaceAll("room_", ""));
    }

}
