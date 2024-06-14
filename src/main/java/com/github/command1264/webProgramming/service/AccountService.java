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
import org.springframework.web.bind.annotation.RequestBody;

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
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindLoginDataOrToken.getErrorMessage());

        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (Exception e) {
            returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getErrorMessage());
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindLoginDataOrToken.getErrorMessage());
        }
        if ((!jsonObject.has(JsonKeyEnum.loginAccount.name()) || !jsonObject.has(JsonKeyEnum.loginPassword.name())) &&
                !jsonObject.has(JsonKeyEnum.token.name())) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindLoginDataOrToken.getErrorMessage());
        }

        String loginAccount = null;
        String loginPassword = null;
        String token = null;
        if (jsonObject.has(JsonKeyEnum.loginAccount.name()))
            loginAccount = jsonObject.get(JsonKeyEnum.loginAccount.name()).getAsString();
        if (jsonObject.has(JsonKeyEnum.loginPassword.name()))
            loginPassword = jsonObject.get(JsonKeyEnum.loginPassword.name()).getAsString();
        if (jsonObject.has(JsonKeyEnum.token.name()))
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
                return returnJsonObject.setSuccessAndErrorMessage(ErrorType.tokenIsExpired.getErrorMessage());
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
                    return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindLoginAccount.getErrorMessage());
                case -2:
                    return returnJsonObject.setSuccessAndErrorMessage(ErrorType.wrongPassword.getErrorMessage());
            }
        }
        return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantLogin.getErrorMessage());
    }

    public ReturnJsonObject changeToken(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (json == null) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindToken.getErrorMessage());
        }

        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getErrorMessage());
        }
        if (!(jsonObject.has(JsonKeyEnum.token.name()) || jsonObject.get(JsonKeyEnum.token.name()).isJsonNull())) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindToken.getErrorMessage());
        }
        String token;
        try {
            token = jsonObject.get(JsonKeyEnum.token.name()).getAsString();
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindToken.getErrorMessage());
        }
        String id = accountDao.getIdWithToken(token);
        if (id == null) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindToken.getErrorMessage());
        }

        int tryCount = 0;
        while(!accountDao.deleteToken(token)) {
            if (++tryCount >= 10) {
                return returnJsonObject.setSuccessAndErrorMessage(ErrorType.changeTokenFailed.getErrorMessage());
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
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getErrorMessage());
        }
        AccountAndRooms accountAndRooms = new AccountAndRooms();
        for(String key : new String[] {"name", "loginAccount", "loginPassword"}) {
            if (JsonChecker.checkKey(jsonObject, key) &&
                    !Objects.equals(jsonObject.get(key).getAsString(), "")) {
                accountAndRooms.set(key, jsonObject.get(key).getAsString());
            } else {
                return returnJsonObject.setSuccessAndErrorMessage(ErrorType.keyNoData.getErrorMessage()
                        .replaceAll(":key", key));
            }
        }
        for (String key : new String[]{"userId", "loginAccount"}) {
            if (sqlDao.checkRepeat(SqlTableEnum.accountInfo.name(), key, accountAndRooms.get(key))) {
                return returnJsonObject.setSuccessAndErrorMessage(ErrorType.findKey.getErrorMessage().replaceAll(":key", key));
            }
        }

        if (!accountDao.createAccount(accountAndRooms)) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantCreateAccount.getErrorMessage());
        }
        return returnJsonObject.setSuccessAndData(true);
    }

    public ReturnJsonObject getContainsUser(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();

        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getErrorMessage());
        }
        String token = null;
        try {
            token = jsonObject.get(JsonKeyEnum.token.name()).getAsString();
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindToken.getErrorMessage());
        }

        String tokenId = accountDao.getIdWithToken(token);
        if (tokenId == null)
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.tokenIsExpired.getErrorMessage());

        String userId = null;
        try {
            userId = jsonObject.get(JsonKeyEnum.name.name()).getAsString();
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindUsers.getErrorMessage());
        }

        try {
            return returnJsonObject.setSuccessAndData(new ArrayList<>(accountDao.getUserContainsUserIdOrName(userId)));
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getErrorMessage());
        }
    }

    public ReturnJsonObject getUser(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();

        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getErrorMessage());
        }
        if ((!jsonObject.has(JsonKeyEnum.id.name()) || jsonObject.get(JsonKeyEnum.id.name()).isJsonNull()) &&
                (!jsonObject.has(JsonKeyEnum.userId.name()) || jsonObject.get(JsonKeyEnum.userId.name()).isJsonNull())) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindIdOrUserId.getErrorMessage());
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
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindAccount.getErrorMessage());
        }
    }

    @Deprecated
    public ReturnJsonObject getAccount(@RequestBody String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();

        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getErrorMessage());
        }
        if ((!jsonObject.has(JsonKeyEnum.id.name()) || jsonObject.get(JsonKeyEnum.id.name()).isJsonNull()) &&
                (!jsonObject.has(JsonKeyEnum.userId.name()) || jsonObject.get(JsonKeyEnum.userId.name()).isJsonNull()) &&
                (!jsonObject.has(JsonKeyEnum.loginAccount.name()) || jsonObject.get(JsonKeyEnum.loginAccount.name()).isJsonNull() ||
                        !jsonObject.has(JsonKeyEnum.loginPassword.name()) || jsonObject.get(JsonKeyEnum.loginPassword.name()).isJsonNull() )) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindIdOrUserId.getErrorMessage());
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
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindAccount.getErrorMessage());
        }
    }

    // todo
    public ReturnJsonObject modifyUser(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();

        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (Exception e) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.dataNotFound.getErrorMessage());
        }
        if (!jsonObject.has(JsonKeyEnum.id.name())) {
            return returnJsonObject.setSuccessAndErrorMessage(ErrorType.cantFindIdOrUserId.getErrorMessage());
        }
        return returnJsonObject;
    }
}
