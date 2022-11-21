package me.alpha432.oyvey.util;

import java.util.HashMap;
import java.util.Map;

public class NicknameUtil {
    private static Map<String, String> nicknames = new HashMap<String, String>();

    public static void addNickname(String name, String nick) {
        nicknames.put(name, nick);
    }

    public static void removeNickname(String name) {
        nicknames.remove(name);
    }

    public static String getNickname(String name) {
        return nicknames.get(name);
    }

    public static boolean hasNickname(String name) {
        return nicknames.containsKey(name);
    }

    public static Map<String, String> getAllNicknames() {
        return nicknames;
    }
}

