package com.github.command1264.webProgramming.service;

import com.github.command1264.webProgramming.accouunt.*;
import com.github.command1264.webProgramming.dao.AccountDao;
import com.github.command1264.webProgramming.dao.SqlDao;
import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.github.command1264.webProgramming.util.JsonChecker;
import com.github.command1264.webProgramming.util.JsonKeyEnum;
import com.github.command1264.webProgramming.util.SqlTableEnum;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Objects;

@Component
public class AccountService {
    Gson gson = new Gson();
    @Autowired
    SqlDao sqlDao;
    @Autowired
    AccountDao accountDao;

    public ReturnJsonObject loginAccount(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (json == null)
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindLoginDataOrToken.getMessage());

        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (Exception e) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getMessage());
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindLoginDataOrToken.getMessage());
        }
        if ((JsonChecker.checkNoKey(jsonObject, JsonKeyEnum.loginAccount.name()) ||
                JsonChecker.checkNoKey(jsonObject, JsonKeyEnum.loginPassword.name())) &&
                JsonChecker.checkNoKey(jsonObject, JsonKeyEnum.token.name())) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindLoginDataOrToken.getMessage());
        }

        String loginAccount = null;
        String loginPassword = null;
        String token = null;
        if (JsonChecker.checkKey(jsonObject, JsonKeyEnum.loginAccount.name()))
            loginAccount = jsonObject.get(JsonKeyEnum.loginAccount.name()).getAsString();
        if (JsonChecker.checkKey(jsonObject, JsonKeyEnum.loginPassword.name()))
            loginPassword = jsonObject.get(JsonKeyEnum.loginPassword.name()).getAsString();
        if (JsonChecker.checkKey(jsonObject, JsonKeyEnum.token.name()))
            token = jsonObject.get(JsonKeyEnum.token.name()).getAsString();


        String id = null;
        if (token != null) {
            id = accountDao.getIdWithToken(token);
            if (id != null) {
                UserAndRooms userAndRooms = accountDao.getUserAndRoomsWithId(id);
                if (userAndRooms != null) {
                    return returnJsonObject.setSuccessAndData(userAndRooms);
                }
            } else {
                return returnJsonObject.setSuccessAndErrorMessage(ErrorType.tokenIsExpired.getMessage());
            }
        }
        if (loginAccount != null && loginPassword != null) {
            switch (accountDao.checkAccount(loginAccount, loginPassword)) {
                case 1:
                    UserAndRooms userAndRooms = accountDao.getUserAndRoomsWithLogin(loginAccount, loginPassword);
                    if (userAndRooms == null) break;

                    Token newToken = accountDao.createToken(userAndRooms.getId());
                    if (newToken == null) break;

                    return returnJsonObject.setSuccessAndData(newToken);
                default:
                case 0:
                    break;
                case -1:
                    return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindLoginAccount.getMessage());
                case -2:
                    return returnJsonObject.setSuccessAndErrorMessage(ErrorType.wrongPassword.getMessage());
            }
        }
        return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantLogin.getMessage());
    }

    public ReturnJsonObject changeToken(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (json == null) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindToken.getMessage());
        }

        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getMessage());
        }
        if (JsonChecker.checkNoKey(jsonObject, JsonKeyEnum.token.name())) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindToken.getMessage());
        }
        String token;
        try {
            token = jsonObject.get(JsonKeyEnum.token.name()).getAsString();
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindToken.getMessage());
        }
        String id = accountDao.getIdWithToken(token);
        if (id == null) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindToken.getMessage());
        }

        int tryCount = 0;
        while(!accountDao.deleteToken(token)) {
            if (++tryCount >= 10) {
                return returnJsonObject.setSuccessAndErrorMessage(ErrorType.changeTokenFailed.getMessage());
            }
        }
        return returnJsonObject.setSuccessAndData(accountDao.createToken(id));


    }

    public ReturnJsonObject createAccount(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getMessage());
        }
        AccountAndRooms accountAndRooms = new AccountAndRooms();
        for(String key : new String[] {"name", "loginAccount", "loginPassword"}) {
            if (JsonChecker.checkKey(jsonObject, key) &&
                    !Objects.equals(jsonObject.get(key).getAsString(), "")) {
                accountAndRooms.set(key, jsonObject.get(key).getAsString());
            } else {
                return returnJsonObject.setSuccessAndErrorMessage(ErrorType.keyNoData.getMessage()
                        .replaceAll(":key", key));
            }
        }
        for (String key : new String[]{/*"userId",*/ "loginAccount"}) {
            if (sqlDao.checkRepeat(SqlTableEnum.accountInfo.name(), key, accountAndRooms.get(key))) {
                return returnJsonObject.setSuccessAndErrorMessage(ErrorType.findKey.getMessage().replaceAll(":key", key));
            }
        }

        if (!accountDao.createAccount(accountAndRooms)) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantCreateAccount.getMessage());
        }
        return returnJsonObject.setSuccessAndData(true);
    }

    public ReturnJsonObject getContainsUser(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();

        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getMessage());
        }
        String token = null;
        try {
            token = jsonObject.get(JsonKeyEnum.token.name()).getAsString();
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindToken.getMessage());
        }

        String tokenId = accountDao.getIdWithToken(token);
        if (tokenId == null)
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.tokenIsExpired.getMessage());

        String userIdOrName = null;
        try {
            userIdOrName = jsonObject.get(JsonKeyEnum.name.name()).getAsString();
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindUsers.getMessage());
        }

        try {
            if (userIdOrName.charAt(0) == '@') {
                return returnJsonObject.setSuccessAndData(accountDao.getUserContainsUserByUserId(userIdOrName.substring(1)));
            } else {
                return returnJsonObject.setSuccessAndData(accountDao.getUserContainsByName(userIdOrName));
            }
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getMessage());
        }
    }

    @Deprecated
    public ReturnJsonObject getUser(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();

        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getMessage());
        }
        if ((!jsonObject.has(JsonKeyEnum.id.name()) || jsonObject.get(JsonKeyEnum.id.name()).isJsonNull()) &&
                (!jsonObject.has(JsonKeyEnum.userId.name()) || jsonObject.get(JsonKeyEnum.userId.name()).isJsonNull())) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindIdOrUserId.getMessage());
        }

        UserAndRooms userAndRooms = null;
        if (jsonObject.has(JsonKeyEnum.id.name()) && !jsonObject.get(JsonKeyEnum.id.name()).isJsonNull()) {
            userAndRooms = accountDao.getUserAndRoomsWithId(jsonObject.get(JsonKeyEnum.id.name()).getAsString());
        }
        if (jsonObject.has(JsonKeyEnum.userId.name()) && !jsonObject.get(JsonKeyEnum.userId.name()).isJsonNull() && userAndRooms == null) {
            userAndRooms = accountDao.getUserAndRoomsWithUserId(jsonObject.get(JsonKeyEnum.userId.name()).getAsString());
        }

        if (userAndRooms != null) {
            return returnJsonObject.setSuccessAndData(userAndRooms);
        } else {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindAccount.getMessage());
        }
    }

    @Deprecated
    public ReturnJsonObject getAccount(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();

        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getMessage());
        }
        if ((!jsonObject.has(JsonKeyEnum.id.name()) || jsonObject.get(JsonKeyEnum.id.name()).isJsonNull()) &&
                (!jsonObject.has(JsonKeyEnum.userId.name()) || jsonObject.get(JsonKeyEnum.userId.name()).isJsonNull()) &&
                (!jsonObject.has(JsonKeyEnum.loginAccount.name()) || jsonObject.get(JsonKeyEnum.loginAccount.name()).isJsonNull() ||
                        !jsonObject.has(JsonKeyEnum.loginPassword.name()) || jsonObject.get(JsonKeyEnum.loginPassword.name()).isJsonNull() )) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindIdOrUserId.getMessage());
        }

        UserAndRooms userAndRooms = null;
        if (jsonObject.has(JsonKeyEnum.id.name()) && !jsonObject.get(JsonKeyEnum.id.name()).isJsonNull()) {
            userAndRooms = accountDao.getUserAndRoomsWithId(jsonObject.get(JsonKeyEnum.id.name()).getAsString());
        }
        if (jsonObject.has(JsonKeyEnum.userId.name()) && !jsonObject.get(JsonKeyEnum.userId.name()).isJsonNull() && userAndRooms == null) {
            userAndRooms = accountDao.getUserAndRoomsWithUserId(jsonObject.get(JsonKeyEnum.userId.name()).getAsString());
        }
        if ((jsonObject.has(JsonKeyEnum.loginAccount.name()) && !jsonObject.get(JsonKeyEnum.loginAccount.name()).isJsonNull()) &&
                (jsonObject.has(JsonKeyEnum.loginPassword.name()) && !jsonObject.get(JsonKeyEnum.loginPassword.name()).isJsonNull()) &&
                userAndRooms == null) {
            userAndRooms = accountDao.getUserAndRoomsWithLogin(jsonObject.get(JsonKeyEnum.loginAccount.name()).getAsString(),
                    jsonObject.get(JsonKeyEnum.loginPassword.name()).getAsString());
        }

        if (userAndRooms != null) {
            return returnJsonObject.setSuccessAndData(userAndRooms);
        } else {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindAccount.getMessage());
        }
    }

    public ReturnJsonObject deleteAccount(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (json == null) return returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getMessage());
        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getMessage());
        }

        if (JsonChecker.checkNoKey(jsonObject, JsonKeyEnum.token.name())) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindToken.getMessage());
        }
        if (JsonChecker.checkNoKey(jsonObject, JsonKeyEnum.userId.name())) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindUsers.name());
        }

        String token;
        try {
            token = jsonObject.get(JsonKeyEnum.token.name()).getAsString();
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindToken.name());
        }

        String tokenId = accountDao.getIdWithToken(token);
        if (tokenId == null)
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.tokenIsExpired.getMessage());

        String userId;
        try {
            userId = jsonObject.get(JsonKeyEnum.userId.name()).getAsString();
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindUsers.getMessage());
        }


        UserAndRooms userAndRooms = accountDao.getUserAndRoomsWithId(tokenId);
        if (userAndRooms == null)
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindUsers.getMessage());

        if (Objects.equals(userAndRooms.getUserId(), userId)) {
            if (accountDao.deleteAccountByUserId(userAndRooms.getUserId())) {
                return returnJsonObject.setSuccessAndData("");
            }
        }

        return returnJsonObject.setSuccessAndErrorMessage(ErrorType.deleteUserFailed.getMessage());
    }

    // todo
    public ReturnJsonObject modifyUser(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();

        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getMessage());
        }
        if (!JsonChecker.checkNoKey(jsonObject, JsonKeyEnum.userIds.name())) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindIdOrUserId.getMessage());
        }
        return returnJsonObject;
    }
}
