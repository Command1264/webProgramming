package com.github.command1264.webProgramming.util;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class RoomNameConverter {
    public static @Nullable UUID getUUID(String name) {
        try {
            return UUID.fromString(name);
        } catch (Exception e) {
            return convertChatRoomName(name);
        }
    }
    public static @Nullable String convertChatRoomName(UUID uuid) {
        if (uuid == null) return null;
        return "room_" + uuid.toString().replaceAll("-", "_");
    }
    public static @Nullable UUID convertChatRoomName(String roomName) {
        if (roomName == null) return null;
        try {
            return UUID.fromString(roomName.replaceAll("room_", "").replaceAll("_", "-"));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    public static boolean isUUID(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
