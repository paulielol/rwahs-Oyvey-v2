package me.alpha432.oyvey.features.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.NameChange;
import me.alpha432.oyvey.features.setting.Setting;

public class NickHider
        extends Module {
    public Setting<Boolean> changeOwn = this.register(new Setting<Boolean>("NameChange", true));
    public final Setting<String> enemyName = this.register(new Setting<String>("Name To Change", "Name to change..."));
    public final Setting<String> enemyNewName = this.register(new Setting<String>("Name", "New Name Here..."));
    private static NickHider instance;

    public NickHider() {
        super("NickHider", "Changes name", Module.Category.CLIENT, false, false, false);
        instance = this;
    }

    @Override
    public void onEnable() {
        Command.sendMessage((Object)ChatFormatting.GREEN + "Success!" + (Object)ChatFormatting.BLUE + " Name succesfully changed to " + (Object)ChatFormatting.GREEN + this.enemyNewName.getValue());
    }

    public static NickHider getInstance() {
        if (instance == null) {
            instance = new NickHider();
        }
        return instance;
    }

    public static String getPlayerName() {
        if (!(!NickHider.fullNullCheck() && !NameChange.getInstance().isConnected())) {
            return mc.getSession().getUsername();
        }
        String name = NameChange.getInstance().getPlayerName();
        if (!(name != null && !name.isEmpty())) {
            return mc.getSession().getUsername();
        }
        return name;
    }
}
