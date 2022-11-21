package me.alpha432.oyvey.util;

import me.alpha432.oyvey.util.IMinecraft;

public class NullUtils
        implements IMinecraft {
    public static boolean nullCheck() {
        return NullUtils.mc.player == null || NullUtils.mc.world == null || NullUtils.mc.playerController == null;
    }
}
