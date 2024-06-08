package com.github.command1264.webProgramming.accouunt;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAndRoomsRowMapper implements RowMapper<UserAndRooms> {
    @Override
    public UserAndRooms mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserAndRooms userAndRooms = new UserAndRooms();
        userAndRooms.setId(rs.getString("id"));
        userAndRooms.setUserId(rs.getString("userId"));
        userAndRooms.setName(rs.getString("name"));
        userAndRooms.setCreateTime(rs.getString("createTime"));
        userAndRooms.setPhotoStickerBase64(rs.getString("photoStickerBase64"));
        userAndRooms.setChatRooms(rs.getString("chatRooms"));
        return userAndRooms;
    }
}
