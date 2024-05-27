package com.github.command1264.webProgramming;

public class User {
    protected String id = "";
    protected String name = "";
    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
    public String getId() {
        return this.id;
    }
    public boolean setName(String name) {
        if (name == null) return false;
        this.name = name;
        return true;
    }
    public boolean setId(String id) {
        if (id == null) return false;
        this.id = id;
        return true;
    }

    public String get(String key) {
        if (key == null) return null;
        return switch (key.toLowerCase()) {
            default -> null;
            case "id" -> this.id;
            case "name" -> this.name;
        };
    }
}
