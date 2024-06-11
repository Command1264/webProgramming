package com.github.command1264.webProgramming.dao;

import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.github.command1264.webProgramming.userChatRoom.UsersChatRoom;
import com.github.command1264.webProgramming.userChatRoom.UsersChatRoomRowMapper;
import com.github.command1264.webProgramming.util.DateTimeFormat;
import com.github.command1264.webProgramming.util.RoomNameConverter;
import com.github.command1264.webProgramming.util.SqlTableEnum;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class UsersChatRoomDao {
    private final Gson gson = new Gson();
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private SqlDao sqlDao;


    public @Nullable UUID getChatRoomUUID(String users) {
        if (jdbcTemplate == null) return null;
        String sql = "select * from :tableName where users=:users;"
                .replaceAll(":tableName", SqlTableEnum.usersChatRooms.name());
        Map<String, Object> map = new HashMap<>() {{
            put("users", users);
        }};
        List<UsersChatRoom> result;
        try {
            result = jdbcTemplate.query(sql, map, new UsersChatRoomRowMapper());
        } catch (Exception e) {
            return null;
        }
        if (result.size() != 1) return null;
        return result.get(0).getUUID();
    }

    public @NotNull List<String> getUsersChatRoomUsers(String chatRoomName) {
        if (jdbcTemplate == null || chatRoomName == null) return new ArrayList<>();
        String sql = "select * from :tableName where uuid=:uuid;"
                .replaceAll(":tableName", SqlTableEnum.usersChatRooms.name());
        Map<String, Object> map = new HashMap<>();
        try {
            UUID chatRoomUUID = UUID.fromString(chatRoomName);
            map.put("uuid", chatRoomUUID.toString());
        } catch (Exception e) {
            map.put("uuid", RoomNameConverter.convertChatRoomName(chatRoomName).toString());
        }
        List<UsersChatRoom> usersChatRoomList;
         try {
             usersChatRoomList = jdbcTemplate.query(sql, map, new UsersChatRoomRowMapper());
         } catch (Exception e) {
             return new ArrayList<>();
         }
        if (usersChatRoomList.size() != 1) return new ArrayList<>();
        return usersChatRoomList.get(0).getUserList();
    }

    @Deprecated
    public @NotNull ReturnJsonObject getUsersChatRoom(String usersListStr) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (jdbcTemplate == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }
        UUID chatUUID = getChatRoomUUID(usersListStr);
        if (chatUUID == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindChatRoom.getErrorMessage());
            return returnJsonObject;
        }
        returnJsonObject.setSuccess(true);
        returnJsonObject.setData(chatUUID.toString());
        return returnJsonObject;
    }

    public @Nullable UsersChatRoom getUsersChatRoom(UUID chatRoomUUID) {
        if (jdbcTemplate == null || chatRoomUUID == null) return null;
        String sql = "select * from :tableName where uuid=:uuid;"
                .replaceAll(":tableName", SqlTableEnum.usersChatRooms.name());
        List<UsersChatRoom> usersChatRoomList = new ArrayList<>();
        try {
            usersChatRoomList =jdbcTemplate.query(sql, new HashMap<>() {{
                put("uuid", chatRoomUUID.toString());
            }}, new UsersChatRoomRowMapper());
        } catch (Exception e) {
            return null;
        }
        if (usersChatRoomList.size() != 1) return null;
        return usersChatRoomList.get(0);
    }

    public @NotNull ReturnJsonObject createUsersChatRoom(String usersListStr, String name) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (jdbcTemplate == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }

        if (sqlDao.checkRepeat(SqlTableEnum.usersChatRooms.name(), "users", usersListStr)) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.chatRoomExist.getErrorMessage());
            return returnJsonObject;
        }
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (sqlDao.checkRepeat(SqlTableEnum.usersChatRooms.name(), "uuid", uuid.toString()));
        String roomName = RoomNameConverter.convertChatRoomName(uuid);

        String insertSql = "insert into :tableName (uuid, name, users, lastModify)  values(:uuid, :name, :users, :lastModify);"
                .replaceAll(":tableName", SqlTableEnum.usersChatRooms.name());
        UUID finalUuid = uuid;
        Map<String, Object> insertMap = new HashMap<>() {{
            put("uuid", finalUuid.toString());
            if (name == null) put("name", finalUuid.toString());
            else put("name", name);
            put("users", usersListStr);
            put("lastModify", LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateTimeFormat.format)));
        }};
        int insertRoomCount;
        try {
            insertRoomCount = jdbcTemplate.update(insertSql, insertMap);
        } catch (Exception e ){
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.sqlUpdateFailed.getErrorMessage());
            return returnJsonObject;
        }

        String createSql = """
            create table :tableName(
                id bigint unsigned not null primary key auto_increment,
                sender bigint unsigned not null,
                message text not null,
                type varchar(20) not null default 'text',
                time datetime not null,
                modify boolean default false,
                deleted boolean default false
            );
        """.replaceAll(":tableName", roomName);

        try {
            jdbcTemplate.update(createSql, new HashMap<>());
        } catch (Exception e) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.sqlUpdateFailed.getErrorMessage());
            return returnJsonObject;
        }

        if (insertRoomCount != 1 || !sqlDao.findTableName(roomName)) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.sqlUpdateFailed.getErrorMessage());
            return returnJsonObject;
        } else {
            returnJsonObject.setSuccessAndData(roomName);
        }
        return returnJsonObject;
    }

    public @NotNull ReturnJsonObject modifyUsersChatRoomUsers(String chatRoomName, String usersListStr) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (jdbcTemplate == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }
        if (usersListStr == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindUsers.getErrorMessage());
            return returnJsonObject;
        }
        String selectSql = "select users from :tableName where uuid=:uuid;"
                .replaceAll(":tableName", SqlTableEnum.usersChatRooms.name());
        Map<String, Object> map = new HashMap<>(){{
            put("uuid", RoomNameConverter.convertChatRoomName(chatRoomName).toString());
        }};
        List userIds;
        try {
            userIds = jdbcTemplate.queryForObject(selectSql, map, List.class);
        } catch (Exception e) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindChatRoom.getErrorMessage());
            return returnJsonObject;
        }

        if (userIds == null) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindChatRoom.getErrorMessage());
            return returnJsonObject;
        }
        if (userIds.size() != 1) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.usersHasMulti.getErrorMessage());
            return returnJsonObject;
        }
        String updateSql = "update :tableName set users=:users where uuid=:uuid;"
                .replaceAll(":tableName", SqlTableEnum.usersChatRooms.name());
        map.put("users", usersListStr);
        int count;
        try {
            count = jdbcTemplate.update(updateSql, map);
        } catch (Exception e) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.usersSaveFailed.getErrorMessage());
            return returnJsonObject;
        }

        if (count != 1) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.usersSaveFailed.getErrorMessage());
            return returnJsonObject;
        }
        returnJsonObject.setSuccessAndData("");
        return returnJsonObject;
    }

    public boolean checkHasUsersChatRoom(String chatRoomName) {
        if (jdbcTemplate == null || chatRoomName == null) return false;
        String sql = "select * from :tableName where uuid=:uuid;"
                .replaceAll(":tableName", SqlTableEnum.usersChatRooms.name());
        UUID chatRoomUUID;
        try {
            chatRoomUUID = UUID.fromString(chatRoomName);
        } catch (Exception e) {
            chatRoomUUID = RoomNameConverter.convertChatRoomName(chatRoomName);
        }
        UUID finalChatRoomUUID = chatRoomUUID;
        Map<String, Object> map = new HashMap<>() {{
            if (finalChatRoomUUID != null) put("uuid", finalChatRoomUUID.toString());
        }};
        List<UsersChatRoom> usersChatRoomList;
        try {
            usersChatRoomList = jdbcTemplate.query(sql, map, new UsersChatRoomRowMapper());
        } catch (Exception e ){
            return false;
        }
        return !usersChatRoomList.isEmpty();

    }


}
