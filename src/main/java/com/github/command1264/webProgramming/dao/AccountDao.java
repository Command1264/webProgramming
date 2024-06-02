package com.github.command1264.webProgramming.dao;

import com.github.command1264.webProgramming.accouunt.Account;
import com.github.command1264.webProgramming.accouunt.AccountRowMapper;
import com.github.command1264.webProgramming.accouunt.User;
import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AccountDao {
    @Autowired
    private SqlDao sqlDao;
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public ReturnJsonObject createAccount(Account account) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (jdbcTemplate == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }

        for (String key : new String[]{"id", "loginAccount"}) {
            if (sqlDao.checkRepeat("accountInfo", key, account.get(key))) {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage(ErrorType.findKey.getErrorMessage().replaceAll(":key", key));
                return returnJsonObject;
            }
        }
        String insertSql = """
            insert into accountInfo (id, name, loginAccount, loginPassword, photoStickerBase64)
            values(:id, :name, :loginAccount, :loginPassword, :photoStickerBase64);
        """;
        Map<String, Object> map = new HashMap<>() {{
            put("id", account.getId());
            put("name", account.getName());
            put("loginAccount", account.getLoginAccount());
            put("loginPassword", account.getLoginPassword());
            put("photoStickerBase64", account.getPhotoStickerBase64());
        }};
        int executeCount = jdbcTemplate.update(insertSql, map);

        if (executeCount != 1) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantCreateAccount.getErrorMessage());
        } else {
            returnJsonObject.setSuccess(true);
        }
        return returnJsonObject;
    }


    public @Nullable User getUser(String id) {
        return (User) getAccount(id);
    }

    public @Nullable Account getAccount(String id) {
        if (jdbcTemplate == null) return null;

        String sql = "select * from accountInfo where id=:id";
        Map<String, Object> map = new HashMap<>() {{
            put("id", id);
        }};

        List<Account> queryList = jdbcTemplate.query(sql, map, new AccountRowMapper());
        if (queryList.size() != 1) return null;
        return queryList.get(0);
    }
    // todo
    public boolean modifyAccount(String oldId, Account account) {
//        if (checkNotConnect()) return false;
//
//        try (Statement stmt = conn.createStatement()) {
//            ResultSet set = stmt.executeQuery(String.format("select * from accountInfo where id='%s'", oldId));
//            int size = 0;
//            while (set.next()) ++size;
//            if (size != 1) return false;
//
//            int executeCount = stmt.executeUpdate(String.format("""
//                    update accountInfo set id='%s',
//                    name='%s',
//                    loginAccount='%s',
//                    loginPassword='%s',
//                    photoStickerBase64='%s'
//                    where id='%s'
//                """,
//                    account.getId(),
//                    account.getName(),
//                    account.getLoginAccount(),
//                    account.getLoginPassword(),
//                    account.getPhotoStickerBase64(),
//                    oldId
//            ));
//            if (executeCount == 1) return true;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        return false;
    }

    public boolean modifyUser(User user) {
        return true;
    }

    // todo
    public boolean deleteAccount(Account account) {
        return true;
    }

    public boolean deleteUser(User user) {
        return true;
    }

}
