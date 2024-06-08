package com.github.command1264.webProgramming.accouunt;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountAndRoomsRowMapper implements RowMapper<AccountAndRooms> {
    @Override
    public AccountAndRooms mapRow(ResultSet rs, int rowNum) throws SQLException {
        AccountAndRooms accountAndRooms = new AccountAndRooms();
        accountAndRooms.setId(rs.getString("id"));
        accountAndRooms.setUserId(rs.getString("userId"));
        accountAndRooms.setName(rs.getString("name"));
        accountAndRooms.setCreateTime(rs.getString("createTime"));
        accountAndRooms.setLoginAccount(rs.getString("loginAccount"));
        accountAndRooms.setLoginPassword(rs.getString("loginPassword"));
        accountAndRooms.setPhotoStickerBase64(rs.getString("photoStickerBase64"));
        accountAndRooms.setChatRooms(rs.getString("chatRooms"));
        return accountAndRooms;
    }
}
