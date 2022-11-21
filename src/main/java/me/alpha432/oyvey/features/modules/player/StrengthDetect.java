package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class StrengthDetect extends Module {

    public static final Minecraft mc = Minecraft.getMinecraft();
    private final Set<EntityPlayer> str = Collections.newSetFromMap(new WeakHashMap());

    public StrengthDetect() {
        super("StrengthDetect", "i am amongus", Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        for (EntityPlayer player : mc.world.playerEntities) {
            if (player.equals(mc.player)) continue;
            if (player.isPotionActive(MobEffects.STRENGTH) && !this.str.contains(player)) {
                Command.sendMessage(player.getDisplayNameString() + " has strength");
                this.str.add(player);
            }

            if (!this.str.contains(player) || player.isPotionActive(MobEffects.STRENGTH)) continue;
            Command.sendMessage(player.getDisplayNameString() + " doesnt have strength");
            this.str.remove(player);
        }
    }
}