package me.alpha432.oyvey.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.util.ColorUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PotionManager
        extends Feature {
    private final Map<EntityPlayer, PotionList> potions = new ConcurrentHashMap<EntityPlayer, PotionList>();

    public List<PotionEffect> getOwnPotions() {
        return this.getPlayerPotions(PotionManager.mc.player);
    }

    public List<PotionEffect> getPlayerPotions(EntityPlayer player) {
        PotionList list = this.potions.get(player);
        List<PotionEffect> potions = new ArrayList<PotionEffect>();
        if (list != null) {
            potions = list.getEffects();
        }
        return potions;
    }

    public String getTextRadarPotionWithDuration(EntityPlayer player) {
        PotionEffect[] array = this.getImportantPotions(player);
        PotionEffect strength = array[0];
        PotionEffect weakness = array[1];
        PotionEffect speed = array[2];
        return "" + (strength != null ? "\u00a7c S" + (strength.getAmplifier() + 1) + " " + Potion.getPotionDurationString(strength, 1.0f) : "") + (weakness != null ? "\u00a78 W " + Potion.getPotionDurationString(weakness, 1.0f) : "") + (speed != null ? "\u00a7b S" + (speed.getAmplifier() + 1) + " " + Potion.getPotionDurationString(Objects.requireNonNull(weakness), 1.0f) : "");
    }

    public String getTextRadarPotion(EntityPlayer player) {
        PotionEffect[] array = this.getImportantPotions(player);
        PotionEffect strength = array[0];
        PotionEffect weakness = array[1];
        PotionEffect speed = array[2];
        return "" + (strength != null ? "\u00a7c S" + (strength.getAmplifier() + 1) + " " : "") + (weakness != null ? "\u00a78 W " : "") + (speed != null ? "\u00a7b S" + (speed.getAmplifier() + 1) + " " : "");
    }

    public PotionEffect[] getImportantPotions(EntityPlayer player) {
        PotionEffect[] array = new PotionEffect[3];
        for (PotionEffect effect : this.getPlayerPotions(player)) {
            Potion potion = effect.getPotion();
            switch (I18n.format(potion.getName()).toLowerCase()) {
                case "strength": {
                    array[0] = effect;
                }
                case "weakness": {
                    array[1] = effect;
                }
                case "speed": {
                    array[2] = effect;
                }
            }
        }
        return array;
    }

    public String getPotionString(PotionEffect effect) {
        Potion potion = effect.getPotion();
        return I18n.format(potion.getName()) + " " + (effect.getAmplifier() + 1) + " " + ChatFormatting.WHITE + Potion.getPotionDurationString(effect, 1.0f);
    }

    public String getColoredPotionString(PotionEffect effect) {
        Potion potion = effect.getPotion();
        switch (I18n.format(potion.getName(), new Object[0])) {
            case "Jump Boost":
            case "Speed": {
                return "\u00a7b" + this.getPotionString(effect);
            }
            case "Resistance":
            case "Strength": {
                return "\u00a7c" + this.getPotionString(effect);
            }
            case "Wither":
            case "Slowness":
            case "Weakness": {
                return "\u00a70" + this.getPotionString(effect);
            }
            case "Absorption": {
                return "\u00a79" + this.getPotionString(effect);
            }
            case "Haste":
            case "Fire Resistance": {
                return "\u00a76" + this.getPotionString(effect);
            }
            case "Regeneration": {
                return "\u00a7d" + this.getPotionString(effect);
            }
            case "Night Vision":
            case "Poison": {
                return "\u00a7a" + this.getPotionString(effect);
            }
        }
        return this.getPotionString(effect);
    }

    public static class PotionList {
        private final List<PotionEffect> effects = new ArrayList<PotionEffect>();

        public void addEffect(PotionEffect effect) {
            if (effect != null) {
                this.effects.add(effect);
            }
        }

        public List<PotionEffect> getEffects() {
            return this.effects;
        }
    }
}


