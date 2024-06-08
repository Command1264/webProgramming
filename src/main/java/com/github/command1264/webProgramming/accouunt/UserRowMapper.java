package com.github.command1264.webProgramming.accouunt;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new Account();
        user.setId(rs.getString("id"));
        user.setUserId(rs.getString("userId"));
        user.setName(rs.getString("name"));
        user.setCreateTime(rs.getString("createTime"));
        user.setPhotoStickerBase64(rs.getString("photoStickerBase64"));
        return user;
    }
}
