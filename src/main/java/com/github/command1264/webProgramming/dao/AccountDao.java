package com.github.command1264.webProgramming.dao;

import com.github.command1264.webProgramming.accouunt.*;
import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.github.command1264.webProgramming.util.BaseRandomGenerator;
import com.google.gson.Gson;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class AccountDao {
//    private final Gson gson = new Gson();
    @Autowired
    private SqlDao sqlDao;
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    /*
    * 1 = correct
    * 0 = null
    * -1 = cant find (loginAccount or id)
    * -2 = wrong password
    */
    public int checkAccount(@Nullable String loginAccount,
                            @Nullable String loginPassword) {
        if (loginAccount == null || loginPassword == null) return 0;
        String sql = "select * from :tableName where loginAccount=:loginAccount or id=:loginAccount;"
                .replaceAll(":tableName", SqlTableEnum.accountInfo.getName());
        Map<String, Object> map = new HashMap<>() {{
            put("loginAccount", loginAccount);
            put("loginPassword", loginPassword);
        }};
        List<Account> accountList = jdbcTemplate.query(sql, map, new AccountRowMapper());
        if (accountList.size() != 1) return -1;

        if (accountList.get(0).getLoginPassword().equals(loginPassword)) return 1;
        else return -2;
    }

    public String getIdWithLogin(@Nullable String loginAccount,
                                 @Nullable String loginPassword) {
        if (loginAccount == null || loginPassword == null) return null;
        String sql = "select * from :tableName where loginAccount=:loginAccount or id=:loginAccount;"
                .replaceAll(":tableName", SqlTableEnum.accountInfo.getName());
        Map<String, Object> map = new HashMap<>() {{
            put("loginAccount", loginAccount);
            put("loginPassword", loginPassword);
        }};
        List<Account> accountList = jdbcTemplate.query(sql, map, new AccountRowMapper());
        if (accountList.size() != 1) return null;

        Account account = accountList.get(0);
        if (account.getLoginPassword().equals(loginPassword)) return account.getId();
        else return null;

    }

    public @Nullable String getIdWithSession(@Nullable String session) {
        if (session == null) return null;
        String sql = "select * from :tableName where session=:session;"
                .replaceAll(":tableName", SqlTableEnum.cookieSessions.getName());
        Map<String, Object> map = new HashMap<>() {{
            put("session", session);
        }};
        List<Session> sessionList = jdbcTemplate.query(sql, map, new SessionRowMapper());
        if (sessionList.size() != 1) {
            sql = "delete from :tableName where session=:session;"
                .replaceAll(":tableName", SqlTableEnum.cookieSessions.getName());
            jdbcTemplate.update(sql, map);
            return null;
        }
        Session tableSession = sessionList.get(0);
        // 如果前面時間比後面時間還晚，回傳 1
        // 如果前面時間跟後面時間一樣，回傳 0
        // 如果前面時間比後面時間還早，回傳 -1
        int type = tableSession.getExpiredTime().compareTo(LocalDateTime.now());
        if (type < 0) {
            sql = "delete from :tableName where session=:session;"
                    .replaceAll(":tableName", SqlTableEnum.cookieSessions.getName());
            jdbcTemplate.update(sql, map);
            return null;
        }
        return tableSession.getId();
    }

    public Session createSession(String id) {
        if (id == null) return null;
        Session session = new Session(id, BaseRandomGenerator.base64(32), LocalDateTime.now().plusDays(1));

        String sql = """
            insert into :tableName(id, session, expiredTime)
            values(:id, :session, :expiredTime);
        """.replaceAll(":tableName", SqlTableEnum.cookieSessions.getName());;
        Map<String, Object> map = new HashMap<>() {{
            put("id", session.getId());
            put("session", session.getSession());
            put("expiredTime", session.getExpiredTime());
        }};
        int count = jdbcTemplate.update(sql, map);
        if (count != 1) return null;
        return session;
    }


    public ReturnJsonObject createAccount(Account account) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (jdbcTemplate == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }

        for (String key : new String[]{"id", "loginAccount"}) {
            if (sqlDao.checkRepeat(SqlTableEnum.accountInfo.getName(), key, account.get(key))) {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage(ErrorType.findKey.getErrorMessage().replaceAll(":key", key));
                return returnJsonObject;
            }
        }
        String insertSql = """
            insert into :tableName (id, name, createTime, loginAccount, loginPassword, photoStickerBase64, chatRooms)
            values(:id, :name, :createTime, :loginAccount, :loginPassword, :photoStickerBase64, :chatRooms);
        """.replaceAll(":tableName", SqlTableEnum.accountInfo.getName());

        Map<String, Object> map = new HashMap<>() {{
            put("id", account.getId());
            put("name", account.getName());
            put("createTime", account.getCreateTime());
            put("loginAccount", account.getLoginAccount());
            put("loginPassword", account.getLoginPassword());
            put("photoStickerBase64", account.getPhotoStickerBase64());
            put("chatRooms", new Gson().toJson(account.getChatRooms()));
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

        String sql = "select * from :tableName where id=:id"
                .replaceAll(":tableName", SqlTableEnum.accountInfo.getName());
        Map<String, Object> map = new HashMap<>() {{
            put("id", id);
        }};

        List<Account> queryList = jdbcTemplate.query(sql, map, new AccountRowMapper());
        if (queryList.size() != 1) return null;
        return queryList.get(0);
    }

    // todo need test
    public boolean modifyAccount(String oldId, Account newAccount) {
        if (jdbcTemplate == null) return false;

        String selectSql = "select * from :tableName where id=:oldId;"
                .replaceAll(":tableName", SqlTableEnum.accountInfo.getName());
        Map<String, Object> map = new HashMap<>() {{
            put("oldId", oldId);
        }};
        List<Account> accountList = jdbcTemplate.query(selectSql, map, new AccountRowMapper());
        if (accountList.size() != 1) return false;

        StringBuilder updateSql = new StringBuilder("update :tableName set id=:id"
                .replaceAll(":tableName", SqlTableEnum.accountInfo.getName()));
        map.put("id", newAccount.getId());
        for(String key : new String[]{"name", "loginAccount", "loginPassword", "photoStickerBase64"}) {
            if (newAccount.get(key) != null || !Objects.equals(newAccount.get(key), "")) {
                map.put(key, newAccount.get(key));
                updateSql.append(MessageFormat.format(", {0}=:{0}", key));
            }
        }
        updateSql.append(" where id=:oldId;");

        int count = jdbcTemplate.update(updateSql.toString(), map);
        return (count == 1);
    }

    // todo need test
    public boolean deleteAccount(Account account) {
        if (account == null) return false;
        return this.deleteAccount(account.getId());
    }

    public boolean deleteAccount(String id) {
        if (jdbcTemplate == null) return false;
        if (id == null || Objects.equals(id, "")) return false;

        String selectSql = "select * from :tableName where id=:id;"
                .replaceAll(":tableName", SqlTableEnum.accountInfo.getName());
        Map<String, Object> map = new HashMap<>() {{
            put("id", id);
        }};
        List<Account> accountList = jdbcTemplate.query(selectSql, map, new AccountRowMapper());
        if (accountList.size() != 1) return false;

        int count = jdbcTemplate.update("delete from :tableName where id=:id;".replaceAll(":tableName", SqlTableEnum.accountInfo.getName()), map);
        return (count == 1);
    }

    public boolean addChatRooms(String id, String chatRoomName) {
        if (id == null || chatRoomName == null || jdbcTemplate == null) return false;

        String selectSql = "select * from :tableName where id=:id;"
                .replaceAll(":tableName", SqlTableEnum.accountInfo.getName());
        Map<String, Object> map = new HashMap<>() {{
            put("id", id);
        }};

        List<Account> accountList = jdbcTemplate.query(selectSql, map, new AccountRowMapper());
        if (accountList.size() != 1) return false;

        Account account = accountList.get(0);
        String updateSql = "update :tableName set chatRooms=:chatRooms where id=:id;"
                .replaceAll(":tableName", SqlTableEnum.accountInfo.getName());

        List<Map<String, Long>> chatRooms = account.getChatRooms();
        chatRooms.add(new HashMap<>(){{
            put(chatRoomName, 0L);
        }});

        int count = jdbcTemplate.update(updateSql, map);
        return count > 0;
    }

}
