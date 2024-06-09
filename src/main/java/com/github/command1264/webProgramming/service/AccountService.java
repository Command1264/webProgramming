package com.github.command1264.webProgramming.service;

import com.github.command1264.webProgramming.accouunt.*;
import com.github.command1264.webProgramming.dao.AccountDao;
import com.github.command1264.webProgramming.dao.SqlDao;
import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.github.command1264.webProgramming.util.JsonKeyEnum;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

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
        returnJsonObject.setSuccess(false);
        returnJsonObject.setErrorMessage(ErrorType.cantFindLoginDataOrToken.getErrorMessage());
        if (json == null) return returnJsonObject;

        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (JsonSyntaxException e) {
            return returnJsonObject;
        }
        if ((!jsonObject.has(JsonKeyEnum.loginAccount.name()) || !jsonObject.has(JsonKeyEnum.loginPassword.name())) &&
                !jsonObject.has(JsonKeyEnum.token.name())) return returnJsonObject;

        String loginAccount = null;
        String loginPassword = null;
        String token = null;
        if (jsonObject.has(JsonKeyEnum.loginAccount.name()))  loginAccount = jsonObject.get(JsonKeyEnum.loginAccount.name()).getAsString();
        if (jsonObject.has(JsonKeyEnum.loginPassword.name()))  loginPassword = jsonObject.get(JsonKeyEnum.loginPassword.name()).getAsString();
        if (jsonObject.has(JsonKeyEnum.token.name()))  token = jsonObject.get(JsonKeyEnum.token.name()).getAsString();


        String id = null;
        if (token != null) {
            id = accountDao.getIdWithToken(token);
            if (id != null) {
                UserAndRooms userAndRooms = accountDao.getUserAndRoomsWithId(id);
                if (userAndRooms != null) {
                    returnJsonObject.setSuccess(true);
                    returnJsonObject.setData(userAndRooms);
                    returnJsonObject.setErrorMessage("");
                    returnJsonObject.setException("");
                    return returnJsonObject;
                }
            } else {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage(ErrorType.tokenIsExpired.getErrorMessage());
                return returnJsonObject;
            }
        }
        if (loginAccount != null && loginPassword != null) {
            switch (accountDao.checkAccount(loginAccount, loginPassword)) {
                case 1:
//                    JsonObject userAndRoomsAndTokenJsonObject = new JsonObject();
                    UserAndRooms userAndRooms = accountDao.getUserAndRoomsWithLogin(loginAccount, loginPassword);
                    if (userAndRooms == null) break;
//                    userAndRoomsAndTokenJsonObject.addProperty(JsonKeyEnum.userAndRooms.name(), userAndRooms.serialize());

                    Token newToken = accountDao.createToken(userAndRooms.getId());
                    if (newToken == null) break;
//                        userAndRoomsAndTokenJsonObject.addProperty(JsonKeyEnum.token.name(), newToken.serialize());
//                    }

                    returnJsonObject.setSuccess(true);
                    returnJsonObject.setData(newToken);
                    returnJsonObject.setErrorMessage("");
                    returnJsonObject.setException("");
                    break;
                default:
                case 0:
                    break;
                case -1:
                    returnJsonObject.setSuccess(false);
                    returnJsonObject.setErrorMessage(ErrorType.cantFindLoginAccount.getErrorMessage());
                    break;
                case -2:
                    returnJsonObject.setSuccess(false);
                    returnJsonObject.setErrorMessage(ErrorType.wrongPassword.getErrorMessage());
                    break;
            }
        }
        return returnJsonObject;
    }

    public ReturnJsonObject changeToken(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (json == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindToken.getErrorMessage());
            return returnJsonObject;
        }
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        if (!(jsonObject.has(JsonKeyEnum.token.name()) || jsonObject.get(JsonKeyEnum.token.name()).isJsonNull())) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindToken.getErrorMessage());
            return returnJsonObject;
        }
        String token;
        try {
            token = jsonObject.get(JsonKeyEnum.token.name()).getAsString();
        } catch (Exception e) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindToken.getErrorMessage());
            return returnJsonObject;
        }
        String id = accountDao.getIdWithToken(token);
        if (id == null) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindToken.getErrorMessage());
            return returnJsonObject;
        }

        int tryCount = 0;
        while(!accountDao.deleteToken(token)) {
            if (++tryCount >= 10) {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage(ErrorType.changeTokenFailed.getErrorMessage());
                return returnJsonObject;
            }
        }
        Token newToken = accountDao.createToken(id);
        returnJsonObject.setSuccess(true);
        returnJsonObject.setData(newToken);
        return returnJsonObject;


    }

    public ReturnJsonObject createAccount(String json) {
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        AccountAndRooms accountandRooms = new AccountAndRooms();
        for(String key : new String[] {"name", "loginAccount", "loginPassword"}) {
            if (jsonObject.has(key) &&
                    !jsonObject.get(key).isJsonNull() &&
                    !Objects.equals(jsonObject.get(key).getAsString(), "")) {
                accountandRooms.set(key, jsonObject.get(key).getAsString());
            } else {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage(ErrorType.keyNoData.getErrorMessage()
                        .replaceAll(":key", key));
                return returnJsonObject;
            }
        }
        return accountDao.createAccount(accountandRooms);
    }

    @Deprecated
    public ReturnJsonObject getUser(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if(sqlDao.checkNotConnect()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        if ((!jsonObject.has(JsonKeyEnum.id.name()) || jsonObject.get(JsonKeyEnum.id.name()).isJsonNull()) &&
                (!jsonObject.has(JsonKeyEnum.userId.name()) || jsonObject.get(JsonKeyEnum.userId.name()).isJsonNull())) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindIdOrUserId.getErrorMessage());
            return returnJsonObject;
        }

        UserAndRooms userAndRooms = null;
        if (jsonObject.has(JsonKeyEnum.id.name()) && !jsonObject.get(JsonKeyEnum.id.name()).isJsonNull()) {
            userAndRooms = accountDao.getUserAndRoomsWithId(jsonObject.get(JsonKeyEnum.id.name()).getAsString());
        }
        if (jsonObject.has(JsonKeyEnum.userId.name()) && !jsonObject.get(JsonKeyEnum.userId.name()).isJsonNull() && userAndRooms == null) {
            userAndRooms = accountDao.getUserAndRoomsWithUserId(jsonObject.get(JsonKeyEnum.userId.name()).getAsString());
        }

        if (userAndRooms != null) {
            returnJsonObject.setSuccess(true);
            returnJsonObject.setData(userAndRooms);
        } else {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindAccount.getErrorMessage());
        }
        return returnJsonObject;
    }

    public ReturnJsonObject getAccount(@RequestBody String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if(sqlDao.checkNotConnect()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        if ((!jsonObject.has(JsonKeyEnum.id.name()) || jsonObject.get(JsonKeyEnum.id.name()).isJsonNull()) &&
                (!jsonObject.has(JsonKeyEnum.userId.name()) || jsonObject.get(JsonKeyEnum.userId.name()).isJsonNull()) &&
                (!jsonObject.has(JsonKeyEnum.loginAccount.name()) || jsonObject.get(JsonKeyEnum.loginAccount.name()).isJsonNull() ||
                        !jsonObject.has(JsonKeyEnum.loginPassword.name()) || jsonObject.get(JsonKeyEnum.loginPassword.name()).isJsonNull() )) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindIdOrUserId.getErrorMessage());
            return returnJsonObject;
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
            returnJsonObject.setSuccess(true);
            returnJsonObject.setData(userAndRooms);
        } else {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindAccount.getErrorMessage());
        }
        return returnJsonObject;
    }

    // todo
    public ReturnJsonObject modifyUser(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if(sqlDao.checkNotConnect()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        if (!jsonObject.has(JsonKeyEnum.id.name())) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindIdOrUserId.getErrorMessage());
            return returnJsonObject;
        }
        return returnJsonObject;
    }
}
