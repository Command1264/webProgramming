package com.github.command1264.webProgramming.userChatRoom;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserChatRoomRowMapper implements RowMapper<UserChatRoom> {
    @Override
    public UserChatRoom mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserChatRoom userChatRoom = new UserChatRoom();
        userChatRoom.setUUID(rs.getString("uuid"));
        userChatRoom.setName(rs.getString("name"));
        userChatRoom.setUsers(rs.getString("users"));
        userChatRoom.setLastModify(rs.getString("lastModify"));
        return userChatRoom;
    }
}
