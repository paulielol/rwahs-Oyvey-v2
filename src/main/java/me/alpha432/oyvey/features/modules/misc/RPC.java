package me.alpha432.oyvey.features.modules.misc;

import me.alpha432.oyvey.DiscordPresence;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;

public class RPC extends Module {

    public static RPC INSTANCE;
    public Setting<Boolean> showIP = (Setting<Boolean>) this.register(new Setting("ShowIP", false, "Shows the server IP in your discord presence."));
    public Setting<String> state = (Setting<String>) this.register(new Setting("State", "In a match", "Sets the state of the DiscordRPC."));
    public Setting<String> largeImageText = (Setting<String>) this.register(new Setting("LargeImageText", "2k21 Patch 7", "Sets the large image text of the DiscordRPC."));
    public Setting<String> smallImageText = (Setting<String>) this.register(new Setting("SmallImageText", "Playing 2k21 via Epic Games", "Sets the small image text of the DiscordRPC."));
    public Setting<LargeImage> largeImage = this.register(new Setting<Object>("LargeImage", LargeImage.blazers));
    public Setting<SmallImage> smallImage = this.register(new Setting<Object>("SmallImage", SmallImage.epicgames));
    public LargeImage lastLargeImage;
    public SmallImage lastSmallImage;

    public RPC() {
        super("RPC", "Discord rich presence", Category.MISC, false, false, false);

        RPC.INSTANCE = this;
    }

    @Override
    public void onEnable() {
        DiscordPresence.start();
    }

    @Override
    public void onUpdate() {
        if (lastLargeImage != largeImage.getValue() || lastSmallImage != smallImage.getValue()) {
            DiscordPresence.stop();
            DiscordPresence.start();
        }
        lastLargeImage = largeImage.getValue();
        lastSmallImage = smallImage.getValue();
    }

    @Override
    public void onDisable() {
        DiscordPresence.stop();
    }

    public enum LargeImage {
        blazers,
        lakers,
        neworleans,
        kobe
    }

    public enum SmallImage {
        steam,
        epicgames
    }

}