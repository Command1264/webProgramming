package com.github.command1264.service;

import com.github.command1264.dao.SqlDao;
import com.github.command1264.accouunt.Account;
import com.github.command1264.accouunt.User;
import com.github.command1264.messages.ReturnJsonObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.web.bind.annotation.RequestBody;

public class AccountService {
    Gson gson = new Gson();
    SqlDao sqlDao;

    public AccountService(Gson gson, SqlDao sqlDao) {
        this.gson = gson;
        this.sqlDao = sqlDao;
    }

    public ReturnJsonObject createAccount(String json) {
        Account account = gson.fromJson(json, Account.class);
        return sqlDao.createAccount(account);
    }

    public ReturnJsonObject getUser(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (!sqlDao.checkNotConnect()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("未連接資料庫");
            return returnJsonObject;
        }
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        if (!jsonObject.has("id")) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("找不到id");
            return returnJsonObject;
        }

        User user = sqlDao.getUser(jsonObject.get("id").getAsString());
        if (user != null) {
            returnJsonObject.setSuccess(true);
            returnJsonObject.setData(gson.toJson(user, User.class));
        } else {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("找不到帳戶");
        }
        return returnJsonObject;
    }

    public ReturnJsonObject getAccount(@RequestBody String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (!sqlDao.checkNotConnect()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("未連接資料庫");
            return returnJsonObject;
        }
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        if (!jsonObject.has("id")) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("找不到id");
            return returnJsonObject;
        }

        Account account = sqlDao.getAccount(jsonObject.get("id").getAsString());
        if (account != null) {
            returnJsonObject.setSuccess(true);
            returnJsonObject.setData(gson.toJson(account, Account.class));
        } else {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("找不到帳戶");
        }
        return returnJsonObject;
    }

    public ReturnJsonObject modifyUser(String json) {
        ReturnJsonObject returnJsonObject = new ReturnJsonObject();
        if (!sqlDao.checkNotConnect()) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("未連接資料庫");
            return returnJsonObject;
        }
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        if (!jsonObject.has("id")) {
            returnJsonObject.setSuccess(false);
            returnJsonObject.setErrorMessage("找不到id");
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
