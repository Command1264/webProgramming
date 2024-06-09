package com.github.command1264.webProgramming.util;

import com.github.command1264.webProgramming.accouunt.User;
import com.github.command1264.webProgramming.dao.AccountDao;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class UsersSorter {
    @Autowired
    private AccountDao accountDao;
    private final Gson gson = new Gson();
    public @Nullable String sortUserIds(JsonArray users) {
        List<String> usersIdList = new ArrayList<>();

        for (JsonElement jsonElement : users.asList()) {
            try {
                User user = accountDao.getUserAndRoomsWithUserId(jsonElement.getAsString());
                if (user == null) continue;
                if (usersIdList.contains(user.getUserId())) continue;
                usersIdList.add(user.getUserId());
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        if (usersIdList.isEmpty()) {
            return null;
        }
        // 排序，讓之後的聊天室更好判斷
//        usersList.sort(Comparator.comparing((User user) -> user.id));
        usersIdList.sort(Comparator.naturalOrder());
        return gson.toJson(gson.toJsonTree(usersIdList, new TypeToken<List<String>>() {
        }.getType()).getAsJsonArray(), JsonArray.class);
    }
    public @Nullable String sortUserIdsReturnIds(JsonArray users) {
        List<String> usersIdList = new ArrayList<>();

        for (JsonElement jsonElement : users.asList()) {
            try {
                User user = accountDao.getUserAndRoomsWithUserId(jsonElement.getAsString());
                if (user == null) continue;
                if (usersIdList.contains(user.getId())) continue;
                usersIdList.add(user.getId());
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        if (usersIdList.isEmpty()) {
            return null;
        }
        // 排序，讓之後的聊天室更好判斷
//        usersList.sort(Comparator.comparing((User user) -> user.id));
        usersIdList.sort(Comparator.naturalOrder());
        return gson.toJson(gson.toJsonTree(usersIdList, new TypeToken<List<String>>() {
        }.getType()).getAsJsonArray(), JsonArray.class);
    }

    public @Nullable String sortIds(JsonArray users) {
        List<String> usersIdList = new ArrayList<>();

        for (JsonElement jsonElement : users.asList()) {
            try {
                User user = accountDao.getUserAndRoomsWithUserId(jsonElement.getAsString());
                if (user == null) continue;
                if (usersIdList.contains(user.getId())) continue;
                usersIdList.add(user.getId());
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        if (usersIdList.isEmpty()) {
            return null;
        }
        // 排序，讓之後的聊天室更好判斷
//        usersList.sort(Comparator.comparing((User user) -> user.id));
        usersIdList.sort(Comparator.naturalOrder());
        return gson.toJson(gson.toJsonTree(usersIdList, new TypeToken<List<String>>() {
        }.getType()).getAsJsonArray(), JsonArray.class);
    }
}
