package com.github.command1264.webProgramming.service;

import com.github.command1264.webProgramming.accouunt.Account;
import com.github.command1264.webProgramming.accouunt.Token;
import com.github.command1264.webProgramming.accouunt.User;
import com.github.command1264.webProgramming.dao.AccountDao;
import com.github.command1264.webProgramming.dao.SqlDao;
import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Objects;

@Component
public class AccountService { // todo 更新 accountInfo 相關
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
        if ((!jsonObject.has("loginAccount") || !jsonObject.has("loginPassword")) &&
                !jsonObject.has("token")) return returnJsonObject;

        String loginAccount = null;
        String loginPassword = null;
        String token = null;
        if (jsonObject.has("loginAccount"))  loginAccount = jsonObject.get("loginAccount").getAsString();
        if (jsonObject.has("loginPassword"))  loginPassword = jsonObject.get("loginPassword").getAsString();
        if (jsonObject.has("token"))  token = jsonObject.get("token").getAsString();


        String id = null;
        if (token != null) {
            id = accountDao.getIdWithToken(token);
            if (id != null) {
                Account account = accountDao.getAccountWithId(id);
                if (account != null) {
                    returnJsonObject.setSuccess(true);
                    returnJsonObject.setData(account.serialize());
                    returnJsonObject.setErrorMessage("");
                    returnJsonObject.setException("");
                    return returnJsonObject;
                }
            }
        }
        if (loginAccount != null && loginPassword != null) {
            switch (accountDao.checkAccount(loginAccount, loginPassword)) {
                case 1:
                    JsonObject accountAnsSessionJsonObject = new JsonObject();
                    Account account = accountDao.getAccountWithLogin(loginAccount, loginPassword);
                    if (account == null) break;
                    accountAnsSessionJsonObject.addProperty("account", account.serialize());

                    Token newToken = accountDao.createSession(account.getId());
                    if (newToken != null) {
                        accountAnsSessionJsonObject.addProperty("token", newToken.serialize());
                    }

                    returnJsonObject.setSuccess(true);
                    returnJsonObject.setData(gson.toJson(accountAnsSessionJsonObject, JsonObject.class));
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

    public ReturnJsonObject createAccount(String json) {
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        Account account = new Account();
        for(String key : new String[] {"name", "loginAccount", "loginPassword"}) {
            if (jsonObject.has(key) &&
                    !jsonObject.get(key).isJsonNull() &&
                    !Objects.equals(jsonObject.get(key).getAsString(), "")) {
                account.set(key, jsonObject.get(key).getAsString());
            } else {
                returnJsonObject.setSuccess(false);
                returnJsonObject.setErrorMessage(ErrorType.keyNoData.getErrorMessage()
                        .replaceAll(":key", key));
                return returnJsonObject;
            }
        }
        return accountDao.createAccount(account);
    }

    public ReturnJsonObject getUser(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if(sqlDao.checkNotConnect()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.sqlNotConnect.getErrorMessage());
            return returnJsonObject;
        }

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        if ((!jsonObject.has("id") || jsonObject.get("id").isJsonNull()) &&
                (!jsonObject.has("userId") || jsonObject.get("userId").isJsonNull())) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindIdOrUserId.getErrorMessage());
            return returnJsonObject;
        }

        User user = null;
        if (jsonObject.has("id") && !jsonObject.get("id").isJsonNull()) {
            user = accountDao.getUserWithId(jsonObject.get("id").getAsString());
        }
        if (jsonObject.has("userId") && !jsonObject.get("userId").isJsonNull() && user == null) {
            user = accountDao.getUserWithUserId(jsonObject.get("userId").getAsString());
        }

        if (user != null) {
            returnJsonObject.setSuccess(true);
            returnJsonObject.setData(gson.toJson(user, User.class));
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
        if ((!jsonObject.has("id") || jsonObject.get("id").isJsonNull()) &&
                (!jsonObject.has("userId") || jsonObject.get("userId").isJsonNull()) &&
                (!jsonObject.has("loginAccount") || jsonObject.get("loginAccount").isJsonNull() ||
                        !jsonObject.has("loginPassword") || jsonObject.get("loginPassword").isJsonNull() )) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindIdOrUserId.getErrorMessage());
            return returnJsonObject;
        }

        Account account = null;
        if (jsonObject.has("id") && !jsonObject.get("id").isJsonNull()) {
            account = accountDao.getAccountWithId(jsonObject.get("id").getAsString());
        }
        if (jsonObject.has("userId") && !jsonObject.get("userId").isJsonNull() && account == null) {
            account = accountDao.getAccountWithUserId(jsonObject.get("userId").getAsString());
        }
        if ((jsonObject.has("loginAccount") && !jsonObject.get("loginAccount").isJsonNull()) &&
                (jsonObject.has("loginPassword") && !jsonObject.get("loginPassword").isJsonNull()) &&
                account == null) {
            account = accountDao.getAccountWithLogin(jsonObject.get("loginAccount").getAsString(),
                                                    jsonObject.get("loginPassword").getAsString());
        }

        if (account != null) {
            returnJsonObject.setSuccess(true);
            returnJsonObject.setData(gson.toJson(account, Account.class));
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
        if (!jsonObject.has("id")) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindIdOrUserId.getErrorMessage());
            return returnJsonObject;
        }

//        boolean flag = sqlDao.getUser(jsonObject.get("id").getAsString());
//        if (!flag) {
//            returnJsonObject.setSuccess(true);
//        } else {
//            returnJsonObject.setSuccess(false);
//            returnJsonObject.setErrorMessage("找不到帳戶");
//        }
        return returnJsonObject;
    }
}
