package com.github.command1264.webProgramming.dao;

import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.github.command1264.webProgramming.userChatRoom.UsersChatRoom;
import com.github.command1264.webProgramming.userChatRoom.UsersChatRoomRowMapper;
import com.github.command1264.webProgramming.util.RoomNameConverter;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        String sql = "select * from :tableName where users=:users;"
                .replaceAll(":tableName", SqlTableEnum.usersChatRooms.getName());
        Map<String, Object> map = new HashMap<>() {{
            put("users", users);
        }};
        List<UsersChatRoom> result = jdbcTemplate.query(sql, map, new UsersChatRoomRowMapper());
        if (result.size() != 1) return null;
        return result.get(0).getUUID();
    }

    public ReturnJsonObject getUsersChatRoom(String usersListStr) {
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

    public ReturnJsonObject createUsersChatRoom(String usersListStr) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (jdbcTemplate == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }

        if (sqlDao.checkRepeat(SqlTableEnum.usersChatRooms.getName(), "users", usersListStr)) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.chatRoomExist.getErrorMessage());
            return returnJsonObject;
        }
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (sqlDao.checkRepeat(SqlTableEnum.usersChatRooms.getName(), "uuid", uuid.toString()));
        String roomName = RoomNameConverter.convertChatRoomName(uuid);

        String insertSql = "insert into :tableName (uuid, users, lastModify)  values(:uuid, :users, :lastModify);"
                .replaceAll(":tableName", SqlTableEnum.usersChatRooms.getName());
        UUID finalUuid = uuid;
        Map<String, Object> insertMap = new HashMap<>() {{
            put("uuid", finalUuid.toString());
            put("users", usersListStr);
            put("lastModify", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS")));
        }};
        int insertRoomCount = jdbcTemplate.update(insertSql, insertMap);

        String createSql = """
            create table :tableName(
                id int not null primary key auto_increment,
                sender varchar(64) not null,
                message text not null,
                type varchar(20) not null default 'text',
                time datetime(4) not null,
                modify boolean default false
            );
        """.replaceAll(":tableName", roomName);

        jdbcTemplate.update(createSql, new HashMap<>());

        if (insertRoomCount != 1 || !sqlDao.findTableName(roomName)) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlUpdateFailed.getErrorMessage());
            return returnJsonObject;
        } else {
            returnJsonObject.setSuccess(true);
        }
        return returnJsonObject;
    }

    public ReturnJsonObject modifyUsersChatRoomUsers(String chatRoomName, String usersListStr) {
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
                .replaceAll(":tableName", SqlTableEnum.usersChatRooms.getName());
        Map<String, Object> map = new HashMap<>(){{
            put("uuid", RoomNameConverter.convertChatRoomName(chatRoomName).toString());
        }};
        List userIds = jdbcTemplate.queryForObject(selectSql, map, List.class);

        if (userIds == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindChatRoom.getErrorMessage());
            return returnJsonObject;
        }
        if (userIds.size() != 1) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.usersHasMulti.getErrorMessage());
            return returnJsonObject;
        }
        String updateSql = "update :tableName set users=:users where uuid=:uuid;"
                .replaceAll(":tableName", SqlTableEnum.usersChatRooms.getName());
        map.put("users", usersListStr);
        int count = jdbcTemplate.update(updateSql, map);

        if (count != 1) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.usersSaveFailed.getErrorMessage());
            return returnJsonObject;
        }
        returnJsonObject.setSuccess(true);
        return returnJsonObject;
    }


}
