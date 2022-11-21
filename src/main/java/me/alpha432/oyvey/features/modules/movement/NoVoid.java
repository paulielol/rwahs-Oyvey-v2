package me.alpha432.oyvey.features.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;

public class NoVoid
        extends Module {
    public Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.BOUNCE));
    public Setting<Boolean> display = this.register(new Setting<Boolean>("Display", true));

    public NoVoid() {
        super("NoVoid", "Glitches you up from void.", Module.Category.MOVEMENT, false, false, false);
    }

    @Override
    public void onUpdate() {
        double yLevel = NoVoid.mc.player.posY;
        if (yLevel <= 0.5) {
            Command.sendMessage(ChatFormatting.RED + "Player " + ChatFormatting.GREEN + NoVoid.mc.player.getName() + ChatFormatting.RED + " is in the void!");
            if (this.mode.getValue().equals((Object)Mode.BOUNCE)) {
                NoVoid.mc.player.moveVertical = 10.0f;
                NoVoid.mc.player.jump();
            }
            if (this.mode.getValue().equals((Object)Mode.LAUNCH)) {
                NoVoid.mc.player.moveVertical = 100.0f;
                NoVoid.mc.player.jump();
            }
        } else {
            NoVoid.mc.player.moveVertical = 0.0f;
        }
    }

    @Override
    public void onDisable() {
        NoVoid.mc.player.moveVertical = 0.0f;
    }

    @Override
    public String getDisplayInfo() {
        if (this.display.getValue().booleanValue()) {
            if (this.mode.getValue().equals((Object)Mode.BOUNCE)) {
                return "Bounce";
            }
            if (this.mode.getValue().equals((Object)Mode.LAUNCH)) {
                return "Launch";
            }
        }
        return null;
    }

    public static enum Mode {
        BOUNCE,
        LAUNCH;

    }
}
