package com.github.command1264.webProgramming.service;

import com.github.command1264.webProgramming.accouunt.Account;
import com.github.command1264.webProgramming.accouunt.AccountConverter;
import com.github.command1264.webProgramming.accouunt.Session;
import com.github.command1264.webProgramming.accouunt.User;
import com.github.command1264.webProgramming.dao.AccountDao;
import com.github.command1264.webProgramming.dao.SqlDao;
import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.github.command1264.webProgramming.util.KeyValueConvertor;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class AccountService { // todo 更新 accountInfo 相關
    Gson gson = new Gson();
    @Autowired
    SqlDao sqlDao;
    @Autowired
    AccountDao accountDao;

    public ReturnJsonObject loginAccount(String json, String cookie) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        returnJsonObject.setSuccess(false);
        returnJsonObject.setErrorMessage(ErrorType.cantFindLoginDataOrSession.getErrorMessage());
        if (json == null) return returnJsonObject;

        JsonObject jsonObject;
        try {
            jsonObject = gson.fromJson(json, JsonObject.class);
        } catch (JsonSyntaxException e) {
            return returnJsonObject;
        }
        if ((!jsonObject.has("loginAccount") || !jsonObject.has("loginPassword")) && cookie == null) return returnJsonObject;

        String loginAccount = null;
        String loginPassword = null;
        if (jsonObject.has("loginAccount"))  loginAccount = jsonObject.get("loginAccount").getAsString();
        if (jsonObject.has("loginPassword"))  loginPassword = jsonObject.get("loginPassword").getAsString();

        String session = null;
        if (cookie != null) {
            JsonObject cookieJsonObject = KeyValueConvertor.toJsonObject(cookie, "; ");
            if (cookieJsonObject.has("sessionId")) session = cookieJsonObject.get("sessionId").getAsString();
        }

        String id = null;
        if (session != null) {
            id = accountDao.getIdWithSession(session);
            if (id != null) {
                returnJsonObject.setSuccess(true);
                returnJsonObject.setData(gson.toJson(accountDao.getAccount(id), Account.class));
                return returnJsonObject;
            }
        }
        if (loginAccount != null && loginPassword != null) {
            switch (accountDao.checkAccount(loginAccount, loginPassword)) {
                case 1:
                    JsonObject accountAnsSessionJsonObject = new JsonObject();
                    Account account = accountDao.getAccount(
                                        accountDao.getIdWithLogin(loginAccount, loginPassword));
                    if (account == null) break;

                    accountAnsSessionJsonObject.addProperty("account",
                            gson.toJson(account, Account.class));
                    Session newSession = accountDao.createSession(account.getId());
                    if (newSession != null) {
                        accountAnsSessionJsonObject.addProperty("session", gson.toJson(newSession, Session.class));
                    }

                    returnJsonObject.setSuccess(true);
                    returnJsonObject.setData(gson.toJson(accountAnsSessionJsonObject, JsonObject.class));
                    break;
                default:
                case 0:
                    break;
                case -1:
                    returnJsonObject.setErrorMessage(ErrorType.cantFindLoginAccount.getErrorMessage());
                    break;
                case -2:
                    returnJsonObject.setErrorMessage(ErrorType.wrongPassword.getErrorMessage());
                    break;
            }
        }
        return returnJsonObject;
    }

    public ReturnJsonObject createAccount(String json) {
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        Account account = new Account();
        for(String key : new String[] {"id", "name", "loginAccount", "loginPassword"}) {
            if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull()) {
                account.set(key, jsonObject.get(key).getAsString());
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
        if (!jsonObject.has("id")) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindId.getErrorMessage());
            return returnJsonObject;
        }

        User user = accountDao.getUser(jsonObject.get("id").getAsString());
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
        if (!jsonObject.has("id")) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindId.getErrorMessage());
            return returnJsonObject;
        }

        Account account = accountDao.getAccount(jsonObject.get("id").getAsString());
        if (account != null) {
            returnJsonObject.setSuccess(true);
            returnJsonObject.setData(gson.toJson(account, Account.class));
        } else {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage(ErrorType.cantFindAccount.getErrorMessage());
        }
        return returnJsonObject;
    }

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
            returnJsonObject.setErrorMessage(ErrorType.cantFindId.getErrorMessage());
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
