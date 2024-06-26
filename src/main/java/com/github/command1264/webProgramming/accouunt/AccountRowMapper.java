package com.github.command1264.webProgramming.accouunt;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountRowMapper implements RowMapper<Account> {
    private boolean hasRawId = false;
    public AccountRowMapper() {
        this(false);
    }
    public AccountRowMapper(boolean hasRawId) {
        this.hasRawId = hasRawId;
    }
    @Override
    public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
        Account account = new Account();
        account.setId((hasRawId) ? rs.getString("id") : "");
        account.setUserId(rs.getString("userId"));
        account.setName(rs.getString("name"));
        account.setCreateTime(rs.getString("createTime"));
        account.setLoginAccount(rs.getString("loginAccount"));
        account.setLoginPassword(rs.getString("loginPassword"));
        account.setPhotoStickerBase64(rs.getString("photoStickerBase64"));
        account.setDeleted(rs.getBoolean("deleted"));
        return account;
    }
}
