package com.github.command1264.webProgramming.accouunt;

import java.time.LocalDateTime;

public class Session {
    String id;
    String session;
    LocalDateTime expiredTime;

    public Session() {
        this("", "", LocalDateTime.now());
    }
    public Session(String id, String session, LocalDateTime expiredTime) {
        this.id = id;
        this.session = session;
        this.expiredTime = expiredTime;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setSession(String session) {
        this.session = session;
    }
    public void setExpiredTime(LocalDateTime expiredTime) {
        this.expiredTime = expiredTime;
    }

    public String getId() {
        return id;
    }
    public String getSession() {
        return session;
    }
    public LocalDateTime getExpiredTime() {
        return expiredTime;
    }

}
