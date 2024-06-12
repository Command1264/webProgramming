package com.github.command1264.webProgramming.dao;

import com.github.command1264.webProgramming.accouunt.*;
import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.github.command1264.webProgramming.util.BaseRandomGenerator;
import com.github.command1264.webProgramming.util.SqlTableEnum;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
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
        List<Account> accountList;
        try {
            accountList = jdbcTemplate.query(sql, map, new AccountRowMapper());
        } catch (Exception e) {
            return 0;
        }
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
        List<Account> accountList;
        try {
            accountList = jdbcTemplate.query(sql, map, new AccountRowMapper());
        } catch (Exception e) {
            return null;
        }
        if (accountList.size() != 1) return null;

        Account account = accountList.get(0);
        if (account.getLoginPassword().equals(loginPassword)) return account.getUserId();
        else return null;

    }

    public @Nullable String getIdWithToken(@Nullable String token) {
        if (jdbcTemplate == null || token == null) return null;
        if (tokenIsExpired(token)) {
            deleteToken(token);
            return null;
        }
        String sql = "select * from :tableName where token=:token;"
                .replaceAll(":tableName", SqlTableEnum.loginTokens.name());
        Map<String, Object> map = new HashMap<>() {{
            put("token", token);
        }};
        List<Token> tokenList;
        try {
            tokenList = jdbcTemplate.query(sql, map, new TokenRowMapper());
        } catch (Exception e) {
            return null;
        }
        if (tokenList.size() != 1) {
            try {
                sql = "delete from :tableName where token=:token;"
                    .replaceAll(":tableName", SqlTableEnum.loginTokens.name());
                jdbcTemplate.update(sql, map);
            } catch (Exception e) {
                return null;
            }
            return null;
        }

        return tokenList.get(0).getId();
    }

    public @Nullable Token createToken(String id) {
        if (jdbcTemplate == null || id == null) return null;

        Token token = checkHasToken(id);
        if (token != null) {
            if (tokenIsExpired(token.getToken())) {
                deleteToken(token.getToken());
            } else {
                return token;
            }
        }

        String checkTokenSql = "select * from :tableName where token=:token;"
                .replaceAll(":tableName", SqlTableEnum.loginTokens.name());
        Map<String, Object> map = new HashMap<>();
        String tokenStr = null;
        List<Token> tokenList = new ArrayList<>();
        int tryCount = 0, generateLength = 32;
        do {
            tokenStr = BaseRandomGenerator.base64(generateLength);
            map.put("token", tokenStr);
            try {
                tokenList = jdbcTemplate.query(checkTokenSql, map, new TokenRowMapper());
            } catch (Exception e) {
                return null;
            }
        } while (!tokenList.isEmpty());

        token = new Token(id, tokenStr, LocalDateTime.now().plusDays(1));

        String sql = """
            replace into :tableName(id, token, expiredTime)
            values(:id, :token, :expiredTime);
        """.replaceAll(":tableName", SqlTableEnum.loginTokens.name());;

        map.put("id", token.getId());
        map.put("token", token.getToken());
        map.put("expiredTime", token.getExpiredTime());

        int count;
        try {
            count = jdbcTemplate.update(sql, map);
        } catch (Exception e) {
            return null;
        }
        if (count != 1) return null;
        return token;
    }

    public @Nullable Token checkHasToken(String id) {
        if (jdbcTemplate == null || id == null) return null;

        String checkUserHasTokenSql = "select * from :tableName where id=:id;"
                .replaceAll(":tableName", SqlTableEnum.loginTokens.name());
        Map<String, Object> map = new HashMap<>() {{
            put("id", id);
        }};
        List<Token> tokenList;
        try {
            tokenList = jdbcTemplate.query(checkUserHasTokenSql, map, new TokenRowMapper());
        } catch (Exception e) {
            return null;
        }
        if (tokenList.size() != 1) return null;
        return tokenList.get(0);


    }


    public @NotNull ReturnJsonObject createAccount(AccountAndRooms accountAndRooms) {
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
            try {
                accountList = jdbcTemplate.query(checkRandomUserIdSql, map, new AccountRowMapper());
            } catch (Exception e) {
                returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantCreateAccount.getErrorMessage());
                return returnJsonObject;
            }

            if (++tryCount >= 10) {
                tryCount = 0;
                ++generateLength;
            }
        } while (!accountList.isEmpty());
        // random generate init userId end

        String insertInfoSql = """
            insert into :tableName (userId, name, createTime, loginAccount, loginPassword, photoStickerBase64)
            values(:userId, :name, :createTime, :loginAccount, :loginPassword, :photoStickerBase64);
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

        int executeInfoCount;
        try {
            executeInfoCount = jdbcTemplate.update(insertInfoSql, map);
        } catch (Exception e) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantCreateAccount.getErrorMessage());
            return returnJsonObject;
        }
        if (executeInfoCount != 1) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantCreateAccount.getErrorMessage());
            return returnJsonObject;
        }

        try {
            accountList = jdbcTemplate.query(selectInfoSql, map, new AccountRowMapper());
        } catch (Exception e) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantCreateAccount.getErrorMessage());
            return returnJsonObject;
        }
        if (accountList.size() != 1) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantCreateAccount.getErrorMessage());
            return returnJsonObject;
        }
        map.put("id", accountList.get(0).getId());

        int executeChatRoomsCount;
        try {
            executeChatRoomsCount = jdbcTemplate.update(insertChatRoomsSql, map);
        } catch (Exception e) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantCreateAccount.getErrorMessage());
            return returnJsonObject;
        }
        if (executeChatRoomsCount != 1) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantCreateAccount.getErrorMessage());
            return returnJsonObject;
        }

        returnJsonObject.setSuccessAndData("");
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

        List<Account> accountList;
        try {
            accountList = jdbcTemplate.query(sql, map, new AccountRowMapper());
        } catch (Exception e) {
            return null;
        }
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

        List<Account> queryList;
        try {
            queryList = jdbcTemplate.query(sql, map, new AccountRowMapper());
        } catch (Exception e) {
            return null;
        }
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

        List<UserAndRooms> userAndRoomsList;
        try {
            userAndRoomsList = jdbcTemplate.query(sql, map, new UserAndRoomsRowMapper());
        } catch (Exception e) {
            return null;
        }
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

        List<AccountAndRooms> accountAndRoomsList;
        try {
            accountAndRoomsList = jdbcTemplate.query(sql, map, new AccountAndRoomsRowMapper());
        } catch (Exception e) {
            return null;
        }
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

        List<UserAndRooms> userAndRoomsList;
        try {
            userAndRoomsList = jdbcTemplate.query(sql, map, new UserAndRoomsRowMapper());
        } catch (Exception e) {
            return null;
        }
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

        List<AccountAndRooms> accountAndRoomsList;
        try {
            accountAndRoomsList = jdbcTemplate.query(sql, map, new AccountAndRoomsRowMapper());
        } catch (Exception e) {
            return null;
        }
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

        List<UserAndRooms> userAndRoomsList;
        try {
            userAndRoomsList = jdbcTemplate.query(sql, map, new UserAndRoomsRowMapper());
        } catch (Exception e) {
            return null;
        }
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

        List<AccountAndRooms> accountAndRoomsList;
        try {
            accountAndRoomsList = jdbcTemplate.query(sql, map, new AccountAndRoomsRowMapper());
        } catch (Exception e) {
            return null;
        }
        if (accountAndRoomsList.size() != 1) return null;
        return accountAndRoomsList.get(0);
    }

    public boolean tokenIsExpired(Token token) {
        return this.tokenIsExpired(token.getToken());
    }
    public boolean tokenIsExpired(String token) {
        if (jdbcTemplate == null || token == null) return true;
        String selectTokenSql = "select * from :tableName where token=:token;"
                .replaceAll(":tableName", SqlTableEnum.loginTokens.name());
        Map<String, Object> map = new HashMap<>() {{
            put("token", token);
        }};
        List<Token> tokenList;
        try {
            tokenList = jdbcTemplate.query(selectTokenSql, map, new TokenRowMapper());
        } catch (Exception e) {
            return true;
        }
        if (tokenList.size() != 1) return true;
        Token tableToken = tokenList.get(0);
        // 如果前面時間比後面時間還晚，回傳 1
        // 如果前面時間跟後面時間一樣，回傳 0
        // 如果前面時間比後面時間還早，回傳 -1
        int type = tableToken.getExpiredTimeWithTime().compareTo(LocalDateTime.now());
        return type < 0;
//        sql = "delete from :tableName where token=:token;"
//                .replaceAll(":tableName", SqlTableEnum.loginTokens.name());
//        jdbcTemplate.update(sql, map);
    }

    public boolean deleteToken(Token token) {
        return this.deleteToken(token.getToken());
    }
    public boolean deleteToken(String token) {
        if (jdbcTemplate == null || token == null) return true;
        String deleteTokenSql = "delete from :tableName where token=:token;"
                .replaceAll(":tableName", SqlTableEnum.loginTokens.name());
        Map<String, Object> map = new HashMap<>() {{
            put("token", token);
        }};
        try {
            int count = jdbcTemplate.update(deleteTokenSql, map);
            return count > 0;
        } catch (Exception e) {
            return false;
        }
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

        int count;
        try {
            count = jdbcTemplate.update(updateSql.toString(), map);
        } catch (Exception e) {
            return false;
        }
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
        List<Account> accountList;
        try {
            accountList = jdbcTemplate.query(selectSql, map, new AccountRowMapper());
        } catch (Exception e) {
            return false;
        }
        if (accountList.size() != 1) return false;

        int count;
        try {
            count = jdbcTemplate.update("delete from :tableName where userId=:userId;"
                    .replaceAll(":tableName", SqlTableEnum.accountInfo.name()), map);
        } catch (Exception e) {
            return false;
        }
        return (count == 1);
    }

    public boolean addChatRoomsWithId(String id, String chatRoomName) {
        if (id == null || chatRoomName == null || jdbcTemplate == null) return false;

        String selectSql = "select * from :tableName where id=:id;"
                .replaceAll(":tableName", SqlTableEnum.accountChatRooms.name());
        Map<String, Object> map = new HashMap<>() {{
            put("id", id);
        }};

        List<AccountChatRooms> accountChatRoomsList;
        try {
            accountChatRoomsList = jdbcTemplate.query(selectSql, map, new AccountRoomRowMapper());
        } catch (Exception e) {
            return false;
        }
        if (accountChatRoomsList.size() != 1) return false;

        AccountChatRooms accountChatRooms = accountChatRoomsList.get(0);
        String updateSql = "update :tableName set chatRooms=:chatRooms where id=:id;"
                .replaceAll(":tableName", SqlTableEnum.accountChatRooms.name());

        accountChatRooms.getChatRooms().put(chatRoomName, 0L);
//        accountChatRooms.getChatRooms().add(new HashMap<>(){{
//            put(chatRoomName, 0L);
//        }});
        map.put("chatRooms", accountChatRooms.getChatRoomsSerialize());

        int count;
        try {
            count = jdbcTemplate.update(updateSql, map);
        } catch (Exception e) {
            return false;
        }
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

        List<AccountAndRooms> accountAndRoomsList;
        try {
            accountAndRoomsList = jdbcTemplate.query(selectSql, map, new AccountAndRoomsRowMapper());
        } catch (Exception e){
            return false;
        }
        if (accountAndRoomsList.size() != 1) return false;

        AccountAndRooms accountAndRooms = accountAndRoomsList.get(0);
        String updateSql = "update :tableName set chatRooms=:chatRooms where id=:id;"
                .replaceAll(":tableName", SqlTableEnum.accountChatRooms.name());

        accountAndRooms.getChatRooms().put(chatRoomName, 0L);
//        accountAndRooms.getChatRooms().add(new HashMap<>(){{
//            put(chatRoomName, 0L);
//        }});;
        map.put("chatRooms", accountAndRooms.getChatRoomsSerialize());

        int count;
        try {
            count = jdbcTemplate.update(updateSql, map);
        } catch (Exception e) {
            return false;
        }
        return count > 0;
    }

    public AccountChatRooms getAccountChatRooms(String id) {
        if (jdbcTemplate == null || id == null) return null;
        String sql = "select * from :tableName where id=:id;"
                .replaceAll(":tableName", SqlTableEnum.accountChatRooms.name());
        List<AccountChatRooms> accountChatRoomsList;
        try {
            accountChatRoomsList = jdbcTemplate.query(sql, new HashMap<>() {{
                put("id", id);
            }}, new AccountRoomRowMapper());
        } catch (Exception e) {
            return null;
        }
        if (accountChatRoomsList.size() != 1) return null;
        return accountChatRoomsList.get(0);
    }

    public boolean setAccountChatRooms(String id, AccountChatRooms accountChatRooms) {
        if (jdbcTemplate == null || id == null ||
                accountChatRooms == null || accountChatRooms.getChatRooms() == null) return false;
        String sql = "update :tableName set chatRooms=:chatRooms where id=:id;"
                .replaceAll(":tableName", SqlTableEnum.accountChatRooms.name());
        int count;
        try {
            count = jdbcTemplate.update(sql, new HashMap<>() {{
                put("id", id);
                put("chatRooms", accountChatRooms.getChatRoomsSerialize());
            }});
        } catch (Exception e) {
            return false;
        }
        return count > 0;
    }

}
