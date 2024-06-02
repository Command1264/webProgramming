package com.github.command1264.webProgramming.service;

import com.github.command1264.webProgramming.accouunt.Account;
import com.github.command1264.webProgramming.accouunt.User;
import com.github.command1264.webProgramming.dao.AccountDao;
import com.github.command1264.webProgramming.dao.SqlDao;
import com.github.command1264.webProgramming.messages.ErrorType;
import com.github.command1264.webProgramming.messages.ReturnJsonObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class AccountService {
    Gson gson = new Gson();
    @Autowired
    SqlDao sqlDao;
    @Autowired
    AccountDao accountDao;

    public ReturnJsonObject createAccount(String json) {
        Account account = gson.fromJson(json, Account.class);
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
