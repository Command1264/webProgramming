package com.github.command1264.webProgramming.accouunt;

import com.github.command1264.webProgramming.util.DateTimeFormat;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Token {
    String id;
    String token;
    String expiredTime;

    public Token() {
        this("", "", LocalDateTime.now());
    }
    public Token(String id, String token, LocalDateTime expiredTime) {
        this.id = id;
        this.token = token;
        this.expiredTime = expiredTime.format(DateTimeFormatter.ofPattern(DateTimeFormat.format));
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public void setExpiredTime(LocalDateTime expiredTime) {
        this.setExpiredTime(expiredTime.format(DateTimeFormatter.ofPattern(DateTimeFormat.format)));
    }
    public void setExpiredTime(String expiredTime) {
        this.expiredTime = expiredTime;
    }

    public String getId() {
        return id;
    }
    public String getToken() {
        return token;
    }
    public String getExpiredTime() {
        return expiredTime;
    }
    public LocalDateTime getExpiredTimeWithTime() {
        return LocalDateTime.parse(expiredTime, DateTimeFormatter.ofPattern(DateTimeFormat.format));
    }


    public String serialize() {
        try {
            return new Gson().toJson(this, Token.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    public static Token deserialize(String json) {
        try {
            return new Gson().fromJson(json, Token.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }
}
