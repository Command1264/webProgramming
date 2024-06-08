package com.github.command1264.webProgramming.dao;

import com.github.command1264.webProgramming.accouunt.*;
import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.github.command1264.webProgramming.userChatRoom.UsersChatRoomRowMapper;
import com.github.command1264.webProgramming.util.BaseRandomGenerator;
import com.github.command1264.webProgramming.util.SqlTableEnum;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class AccountDao {
    private final Gson gson = new Gson();
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
        String sql = StringUtils.replaceEach("""
                select info.id, info.userId,
                        info.name, info.createTime,
                        info.loginAccount, info.loginPassword,
                        info.photoStickerBase64, room.chatRooms
                        from :tableNameInfo info inner join :tableNameChatRooms room
                    on info.id=room.id
                where info.loginAccount=:loginAccount or info.userId=:loginAccount;
        """,
        new String[] {":tableNameInfo", ":tableNameChatRooms"},
        new String[] {SqlTableEnum.accountInfo.name(), SqlTableEnum.accountChatRooms.name()});
//        String sql = "select * from :tableName where loginAccount=:loginAccount or userId=:loginAccount;"
//                .replaceAll(":tableName", SqlTableEnum.accountInfo.name());
        Map<String, Object> map = new HashMap<>() {{
            put("loginAccount", loginAccount);
            put("loginPassword", loginPassword);
        }};
        List<Account> accountList = jdbcTemplate.query(sql, map, new AccountRowMapper());
        if (accountList.size() != 1) return -1;

        if (accountList.get(0).getLoginPassword().equals(loginPassword)) return 1;
        else return -2;
    }

    public String getUserIdWithLogin(@Nullable String loginAccount,
                                     @Nullable String loginPassword) {
        if (loginAccount == null || loginPassword == null) return null;
        String sql = StringUtils.replaceEach("""
                select info.id, info.userId,
                        info.name, info.createTime,
                        info.loginAccount, info.loginPassword,
                        info.photoStickerBase64, room.chatRooms
                        from :tableNameInfo info inner join :tableNameChatRooms room
                    on info.id=room.id
                where info.loginAccount=:loginAccount or info.userId=:loginAccount;
        """,
        new String[] {":tableNameInfo", ":tableNameChatRooms"},
        new String[] {SqlTableEnum.accountInfo.name(), SqlTableEnum.accountChatRooms.name()});
//        String sql = "select * from :tableName where loginAccount=:loginAccount or userId=:loginAccount;"
//                .replaceAll(":tableName", SqlTableEnum.accountInfo.name());
        Map<String, Object> map = new HashMap<>() {{
            put("loginAccount", loginAccount);
            put("loginPassword", loginPassword);
        }};
        List<Account> accountList = jdbcTemplate.query(sql, map, new AccountRowMapper());
        if (accountList.size() != 1) return null;

        Account account = accountList.get(0);
        if (account.getLoginPassword().equals(loginPassword)) return account.getUserId();
        else return null;

    }

    public @Nullable String getIdWithToken(@Nullable String token) {
        if (token == null) return null;
        String sql = "select * from :tableName where token=:token;"
                .replaceAll(":tableName", SqlTableEnum.loginTokens.name());
        Map<String, Object> map = new HashMap<>() {{
            put("token", token);
        }};
        List<Token> tokenList = jdbcTemplate.query(sql, map, new TokenRowMapper());
        if (tokenList.size() != 1) {
            sql = "delete from :tableName where token=:token;"
                .replaceAll(":tableName", SqlTableEnum.loginTokens.name());
            jdbcTemplate.update(sql, map);
            return null;
        }
        Token tableToken = tokenList.get(0);
        // 如果前面時間比後面時間還晚，回傳 1
        // 如果前面時間跟後面時間一樣，回傳 0
        // 如果前面時間比後面時間還早，回傳 -1
        int type = tableToken.getExpiredTimeWithTime().compareTo(LocalDateTime.now());
        if (type < 0) {
            sql = "delete from :tableName where token=:token;"
                    .replaceAll(":tableName", SqlTableEnum.loginTokens.name());
            jdbcTemplate.update(sql, map);
            return null;
        }
        return tableToken.getId();
    }

    public Token createToken(String id) {
        if (jdbcTemplate == null || id == null) return null;

        Token token = checkHasToken(id);
        if (token != null) return token;

        String checkTokenSql = "select * from :tableName where token=:token;"
                .replaceAll(":tableName", SqlTableEnum.loginTokens.name());
        Map<String, Object> map = new HashMap<>();
        String tokenStr = null;
        List<Token> tokenList = new ArrayList<>();
        int tryCount = 0, generateLength = 32;
        do {
            tokenStr = BaseRandomGenerator.base64(generateLength);
            map.put("token", tokenStr);
            tokenList = jdbcTemplate.query(checkTokenSql, map, new TokenRowMapper());
        } while (!tokenList.isEmpty());

        token = new Token(id, tokenStr, LocalDateTime.now().plusDays(1));

        String sql = """
            insert into :tableName(id, token, expiredTime)
            values(:id, :token, :expiredTime);
        """.replaceAll(":tableName", SqlTableEnum.loginTokens.name());;

        map.put("id", token.getId());
        map.put("token", token.getToken());
        map.put("expiredTime", token.getExpiredTime());

        int count = jdbcTemplate.update(sql, map);
        if (count != 1) return null;
        return token;
    }

    public Token checkHasToken(String id) {
        if (jdbcTemplate == null || id == null) return null;

        String checkUserHasTokenSql = "select * from :tableName where id=:id;"
                .replaceAll(":tableName", SqlTableEnum.loginTokens.name());
        Map<String, Object> map = new HashMap<>() {{
            put("id", id);
        }};
        List<Token> tokenList = jdbcTemplate.query(checkUserHasTokenSql, map, new TokenRowMapper());
        if (tokenList.size() != 1) return null;
        return tokenList.get(0);


    }


    public ReturnJsonObject createAccount(AccountAndRooms accountAndRooms) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (jdbcTemplate == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }

        for (String key : new String[]{"userId", "loginAccount"}) {
            if (sqlDao.checkRepeat(SqlTableEnum.accountInfo.name(), key, accountAndRooms.get(key))) {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage(ErrorType.findKey.getErrorMessage().replaceAll(":key", key));
                return returnJsonObject;
            }
        }

        // random generate init userId start
        List<Account> accountList = new ArrayList<>();
        String checkRandomUserIdSql = "select * from :tableName where userId=:userId;"
                .replaceAll(":tableName", SqlTableEnum.accountInfo.name());
        Map<String, Object> map = new HashMap<>();
        String userId = null;
        int tryCount = 0;
        int generateLength = 10;
        do {
            userId = BaseRandomGenerator.base58(generateLength);
            map.put("userId", userId);
            accountList = jdbcTemplate.query(checkRandomUserIdSql, map, new AccountRowMapper());

            if (++tryCount >= 10) {
                tryCount = 0;
                ++generateLength;
            }
        } while (!accountList.isEmpty());
        // random generate init userId end

        String insertInfoSql = """
            insert into :tableName (userId, name, createTime, loginAccount, loginPassword, photoStickerBase64, chatRooms)
            values(:userId, :name, :createTime, :loginAccount, :loginPassword, :photoStickerBase64, :chatRooms);
        """.replaceAll(":tableName", SqlTableEnum.accountInfo.name());

        String selectInfoSql = """
                select * from :tableName where userId=:userId and loginAccount=:loginAccount;
                """.replaceAll(":tableName", SqlTableEnum.accountInfo.name());

        String insertChatRoomsSql = """
            insert into :tableName (id, chatRooms)
            values(:id, :chatRooms);
        """.replaceAll(":tableName", SqlTableEnum.accountChatRooms.name());

        map.put("name", accountAndRooms.getName());
        map.put("createTime", accountAndRooms.getCreateTime());
        map.put("loginAccount", accountAndRooms.getLoginAccount());
        map.put("loginPassword", accountAndRooms.getLoginPassword());
        map.put("photoStickerBase64", accountAndRooms.getPhotoStickerBase64());
        map.put("chatRooms", accountAndRooms.getChatRoomsSerialize());

        int executeInfoCount = jdbcTemplate.update(insertInfoSql, map);
        if (executeInfoCount != 1) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantCreateAccount.getErrorMessage());
            return returnJsonObject;
        }

        accountList = jdbcTemplate.query(selectInfoSql, map, new AccountRowMapper());
        if (accountList.size() != 1) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantCreateAccount.getErrorMessage());
            return returnJsonObject;
        }
        map.put("id", accountList.get(0).getId());

        int executeChatRoomsCount = jdbcTemplate.update(insertChatRoomsSql, map);
        if (executeChatRoomsCount != 1) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantCreateAccount.getErrorMessage());
            return returnJsonObject;
        }

        returnJsonObject.setSuccess(true);
        returnJsonObject.setErrorMessage("");
        return returnJsonObject;
    }


    @Deprecated
    public @Nullable User getUserWithUserId(String userId) {
        return (User) getAccountWithUserId(userId);
    }

    @Deprecated
    public @Nullable Account getAccountWithUserId(String userId) {
        if (jdbcTemplate == null) return null;

        String sql = "select * from :tableName where userId=:userId"
                .replaceAll(":tableName", SqlTableEnum.accountInfo.name());
        Map<String, Object> map = new HashMap<>() {{
            put("userId", userId);
        }};

        List<Account> accountList = jdbcTemplate.query(sql, map, new AccountRowMapper());
        if (accountList.size() != 1) return null;
        return accountList.get(0);
    }

    @Deprecated
    public @Nullable User getUserWithId(String id) {
        return (User) getAccountWithId(id);
    }

    @Deprecated
    public @Nullable Account getAccountWithId(String id) {
        if (jdbcTemplate == null) return null;

        String sql = "select * from :tableName where id=:id"
                .replaceAll(":tableName", SqlTableEnum.accountInfo.name());
        Map<String, Object> map = new HashMap<>() {{
            put("id", id);
        }};

        List<Account> queryList = jdbcTemplate.query(sql, map, new AccountRowMapper());
        if (queryList.size() != 1) return null;
        return queryList.get(0);
    }

    public @Nullable UserAndRooms getUserAndRoomsWithLogin(String loginAccount, String loginPassword) {
        if (jdbcTemplate == null) return null;
        if (loginAccount == null || loginPassword == null) return null;

        String sql = StringUtils.replaceEach("""
                select info.id, info.userId,
                        info.name, info.createTime,
                        info.photoStickerBase64, room.chatRooms
                    from :tableInfo info inner join :tableChatRooms room
                    on info.id=room.id
                where info.loginAccount=:loginAccount and info.loginPassword=:loginPassword;
            """,
                new String[]{":tableInfo", ":tableChatRooms"},
                new String[]{SqlTableEnum.accountInfo.name(), SqlTableEnum.accountChatRooms.name()});
        Map<String, Object> map = new HashMap<>() {{
            put("loginAccount", loginAccount);
            put("loginPassword", loginPassword);
        }};

        List<UserAndRooms> userAndRoomsList = jdbcTemplate.query(sql, map, new UserAndRoomsRowMapper());
        if (userAndRoomsList.size() != 1) return null;
        return userAndRoomsList.get(0);
    }

    public @Nullable AccountAndRooms getAccountAndRoomsWithLogin(String loginAccount, String loginPassword) {
        if (jdbcTemplate == null) return null;
        if (loginAccount == null || loginPassword == null) return null;

        String sql = StringUtils.replaceEach("""
                select info.id, info.userId,
                        info.name, info.createTime,
                        info.loginAccount, info.loginPassword,
                        info.photoStickerBase64, room.chatRooms
                    from :tableInfo info inner join :tableChatRooms room
                    on info.id=room.id
                where info.loginAccount=:loginAccount and info.loginPassword=:loginPassword;
            """,
                new String[]{":tableInfo", ":tableChatRooms"},
                new String[]{SqlTableEnum.accountInfo.name(), SqlTableEnum.accountChatRooms.name()});
        Map<String, Object> map = new HashMap<>() {{
            put("loginAccount", loginAccount);
            put("loginPassword", loginPassword);
        }};

        List<AccountAndRooms> accountAndRoomsList = jdbcTemplate.query(sql, map, new AccountAndRoomsRowMapper());
        if (accountAndRoomsList.size() != 1) return null;
        return accountAndRoomsList.get(0);
    }

    public @Nullable UserAndRooms getUserAndRoomsWithUserId(String userId) {
        if (jdbcTemplate == null || userId == null) return null;

        String sql = StringUtils.replaceEach("""
                select info.id, info.userId,
                        info.name, info.createTime,
                        info.photoStickerBase64, room.chatRooms
                    from :tableInfo info inner join :tableChatRooms room
                    on info.id=room.id
                where info.userId=:userId;
            """,
                new String[]{":tableInfo", ":tableChatRooms"},
                new String[]{SqlTableEnum.accountInfo.name(), SqlTableEnum.accountChatRooms.name()});
        Map<String, Object> map = new HashMap<>() {{
            put("userId", userId);
        }};

        List<UserAndRooms> userAndRoomsList = jdbcTemplate.query(sql, map, new UserAndRoomsRowMapper());
        if (userAndRoomsList.size() != 1) return null;
        return userAndRoomsList.get(0);
    }

    public @Nullable AccountAndRooms getAccountAndRoomsWithUserId(String userId) {
        if (jdbcTemplate == null || userId == null) return null;

        String sql = StringUtils.replaceEach("""
                select info.id, info.userId,
                        info.name, info.createTime,
                        info.loginAccount, info.loginPassword,
                        info.photoStickerBase64, room.chatRooms
                    from :tableInfo info inner join :tableChatRooms room
                    on info.id=room.id
                where info.userId=:userId;
            """,
                new String[]{":tableInfo", ":tableChatRooms"},
                new String[]{SqlTableEnum.accountInfo.name(), SqlTableEnum.accountChatRooms.name()});
        Map<String, Object> map = new HashMap<>() {{
            put("userId", userId);
        }};

        List<AccountAndRooms> accountAndRoomsList = jdbcTemplate.query(sql, map, new AccountAndRoomsRowMapper());
        if (accountAndRoomsList.size() != 1) return null;
        return accountAndRoomsList.get(0);
    }

    public @Nullable UserAndRooms getUserAndRoomsWithId(String id) {
        if (jdbcTemplate == null) return null;

        String sql = StringUtils.replaceEach("""
                select info.id, info.userId,
                        info.name, info.createTime,
                        info.photoStickerBase64, room.chatRooms
                    from :tableInfo info inner join :tableChatRooms room
                    on info.id=room.id
                where info.id=:id;
            """,
                new String[]{":tableInfo", ":tableChatRooms"},
                new String[]{SqlTableEnum.accountInfo.name(), SqlTableEnum.accountChatRooms.name()});

        Map<String, Object> map = new HashMap<>() {{
            put("id", id);
        }};

        List<UserAndRooms> userAndRoomsList = jdbcTemplate.query(sql, map, new UserAndRoomsRowMapper());
        if (userAndRoomsList.size() != 1) return null;
        return userAndRoomsList.get(0);
    }

    public @Nullable AccountAndRooms getAccountAndRoomsWithId(String id) {
        if (jdbcTemplate == null) return null;

        String sql = StringUtils.replaceEach("""
                select info.id, info.userId,
                        info.name, info.createTime,
                        info.loginAccount, info.loginPassword,
                        info.photoStickerBase64, room.chatRooms
                    from :tableInfo info inner join :tableChatRooms room
                    on info.id=room.id
                where info.id=:id;
            """,
            new String[]{":tableInfo", ":tableChatRooms"},
            new String[]{SqlTableEnum.accountInfo.name(), SqlTableEnum.accountChatRooms.name()});

        Map<String, Object> map = new HashMap<>() {{
            put("id", id);
        }};

        List<AccountAndRooms> accountAndRoomsList = jdbcTemplate.query(sql, map, new AccountAndRoomsRowMapper());
        if (accountAndRoomsList.size() != 1) return null;
        return accountAndRoomsList.get(0);
    }


    // todo need test
    public boolean modifyAccount(String oldUserId, Account newAccount) {
        if (jdbcTemplate == null) return false;

        String selectSql = "select * from :tableName where userId=:oldUserId;"
                .replaceAll(":tableName", SqlTableEnum.accountInfo.name());
        Map<String, Object> map = new HashMap<>() {{
            put("oldUserId", oldUserId);
        }};
        List<Account> accountList = jdbcTemplate.query(selectSql, map, new AccountRowMapper());
        if (accountList.size() != 1) return false;

        StringBuilder updateSql = new StringBuilder("update :tableName set userId=:userId"
                .replaceAll(":tableName", SqlTableEnum.accountInfo.name()));
        map.put("userId", newAccount.getUserId());

        for(String key : new String[]{"name", "loginAccount", "loginPassword", "photoStickerBase64"}) {
            if (newAccount.get(key) != null || !Objects.equals(newAccount.get(key), "")) {
                updateSql.append(MessageFormat.format(", {0}=:{0}", key));
                map.put(key, newAccount.get(key));
            }
        }
        updateSql.append(" where userId=:oldUserId;");

        int count = jdbcTemplate.update(updateSql.toString(), map);
        return (count == 1);
    }

    // todo need test
    public boolean deleteAccount(Account account) {
        if (account == null) return false;
        return this.deleteAccount(account.getUserId());
    }

    public boolean deleteAccount(String userId) {
        if (jdbcTemplate == null) return false;
        if (userId == null || Objects.equals(userId, "")) return false;

        String selectSql = "select * from :tableName where userId=:userId;"
                .replaceAll(":tableName", SqlTableEnum.accountInfo.name());
        Map<String, Object> map = new HashMap<>() {{
            put("userId", userId);
        }};
        List<Account> accountList = jdbcTemplate.query(selectSql, map, new AccountRowMapper());
        if (accountList.size() != 1) return false;

        int count = jdbcTemplate.update("delete from :tableName where userId=:userId;".replaceAll(":tableName", SqlTableEnum.accountInfo.name()), map);
        return (count == 1);
    }

    public boolean addChatRoomsWithId(String id, String chatRoomName) {
        if (id == null || chatRoomName == null || jdbcTemplate == null) return false;

        String selectSql = "select * from :tableName where id=:id;"
                .replaceAll(":tableName", SqlTableEnum.accountChatRooms.name());
        Map<String, Object> map = new HashMap<>() {{
            put("id", id);
        }};

        List<AccountRoom> accountRoomList = jdbcTemplate.query(selectSql, map, new AccountRoomRowMapper());
        if (accountRoomList.size() != 1) return false;

        AccountRoom accountRoom = accountRoomList.get(0);
        String updateSql = "update :tableName set chatRooms=:chatRooms where id=:id;"
                .replaceAll(":tableName", SqlTableEnum.accountChatRooms.name());

        accountRoom.getChatRooms().add(new HashMap<>(){{
            put(chatRoomName, 0L);
        }});
        map.put("chatRooms", accountRoom.getChatRoomsSerialize());

        int count = jdbcTemplate.update(updateSql, map);
        return count > 0;
    }

    public boolean addChatRoomsWithUserId(String userId, String chatRoomName) {
        if (userId == null || chatRoomName == null || jdbcTemplate == null) return false;

        String selectSql = StringUtils.replaceEach("""
                select info.id, info.userId,
                            info.name, info.createTime,
                            info.loginAccount, info.loginPassword,
                            info.photoStickerBase64, room.chatRooms
                        from :tableInfo info inner join :tableChatRooms room
                    on info.id=room.id
                where info.userId=:userId;
                """,
        new String[]{":tableInfo", ":tableChatRooms"},
        new String[]{SqlTableEnum.accountInfo.name(), SqlTableEnum.accountChatRooms.name()});
//        String selectSql = "select * from :tableName where userId=:userId;"
//                .replaceAll(":tableName", SqlTableEnum.accountInfo.name());
        Map<String, Object> map = new HashMap<>() {{
            put("userId", userId);
        }};

        List<AccountAndRooms> accountAndRoomsList = jdbcTemplate.query(selectSql, map, new AccountAndRoomsRowMapper());
        if (accountAndRoomsList.size() != 1) return false;

        AccountAndRooms accountAndRooms = accountAndRoomsList.get(0);
        String updateSql = "update :tableName set chatRooms=:chatRooms where id=:id;"
                .replaceAll(":tableName", SqlTableEnum.accountChatRooms.name());

        accountAndRooms.getChatRooms().add(new HashMap<>(){{
            put(chatRoomName, 0L);
        }});;
        map.put("chatRooms", accountAndRooms.getChatRoomsSerialize());

        int count = jdbcTemplate.update(updateSql, map);
        return count > 0;
    }

}
