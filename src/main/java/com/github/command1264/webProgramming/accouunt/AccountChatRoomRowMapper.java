package com.github.command1264.webProgramming.accouunt;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountChatRoomRowMapper implements RowMapper<AccountChatRooms> {
    @Override
    public AccountChatRooms mapRow(ResultSet rs, int rowNum) throws SQLException {
        AccountChatRooms accountChatRooms = new AccountChatRooms();
        accountChatRooms.setId(rs.getString("id"));
        accountChatRooms.setChatRooms(rs.getString("chatRooms"));
        return accountChatRooms;
    }
}
