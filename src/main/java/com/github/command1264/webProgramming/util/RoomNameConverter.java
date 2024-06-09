package com.github.command1264.webProgramming.util;

import java.util.UUID;

public class RoomNameConverter {
    public static String convertChatRoomName(UUID uuid) {
        if (uuid == null) return null;
        return "room_" + uuid.toString().replaceAll("-", "_");
    }
    public static UUID convertChatRoomName(String roomName) {
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
