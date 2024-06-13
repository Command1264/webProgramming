package com.github.command1264.webProgramming.accouunt;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {
    private boolean hasRawId = false;

    public UserRowMapper() {
        this(false);
    }
    public UserRowMapper(boolean hasRawId) {
        this.hasRawId = hasRawId;
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId((hasRawId) ? rs.getString("id") : "");
        user.setUserId(rs.getString("userId"));
        user.setName(rs.getString("name"));
        user.setCreateTime(rs.getString("createTime"));
        user.setPhotoStickerBase64(rs.getString("photoStickerBase64"));
        return user;
    }
}
