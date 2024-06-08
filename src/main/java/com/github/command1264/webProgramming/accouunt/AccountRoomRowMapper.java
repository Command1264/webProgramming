package com.github.command1264.webProgramming.accouunt;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountRoomRowMapper implements RowMapper<AccountRoom> {
    @Override
    public AccountRoom mapRow(ResultSet rs, int rowNum) throws SQLException {
        AccountRoom accountRoom = new AccountRoom();
        accountRoom.setId(rs.getString("id"));
        accountRoom.setChatRooms(rs.getString("chatRooms"));
        return accountRoom;
    }
}
