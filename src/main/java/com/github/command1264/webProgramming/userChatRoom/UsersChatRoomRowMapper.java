package com.github.command1264.webProgramming.userChatRoom;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersChatRoomRowMapper implements RowMapper<UsersChatRoom> {
    @Override
    public UsersChatRoom mapRow(ResultSet rs, int rowNum) throws SQLException {
        UsersChatRoom usersChatRoom = new UsersChatRoom();
        usersChatRoom.setUUID(rs.getString("uuid"));
        usersChatRoom.setUsers(rs.getString("users"));
        return usersChatRoom;
    }
}
