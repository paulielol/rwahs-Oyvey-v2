//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.block.BlockEnderChest
 *  net.minecraft.block.BlockObsidian
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 */
package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.features.command.Command;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class VelocitySpoof
        extends Module {
    private final Setting<Boolean> Bows = this.register(new Setting<Boolean>("Bows", true));
    private final Setting<Boolean> pearls = this.register(new Setting<Boolean>("Pearls", true));
    private final Setting<Boolean> eggs = this.register(new Setting<Boolean>("Eggs", true));
    private final Setting<Boolean> snowballs = this.register(new Setting<Boolean>("Snowballs", true));
    private final Setting<Integer> Timeout = this.register(new Setting<Integer>("Timeout", 5000, 100, 20000));
    private final Setting<Integer> spoofs = this.register(new Setting<Integer>("Spoofs", 10, 1, 300));
    private final Setting<Boolean> bypass = this.register(new Setting<Boolean>("Bypass", false));
    private final Setting<Boolean> debug = this.register(new Setting<Boolean>("Debug", false));

    private boolean shooting;
    private long lastShootTime;

    public VelocitySpoof() {
        super("VelocitySpoof", "Traps other players", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        if ( this.isEnabled()) {
            shooting = false;
            lastShootTime = System.currentTimeMillis();
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getStage() != 0) return;

        if (event.getPacket() instanceof CPacketPlayerDigging) {
            CPacketPlayerDigging packet = (CPacketPlayerDigging) event.getPacket();

            if (packet.getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                ItemStack handStack = mc.player.getHeldItem(EnumHand.MAIN_HAND);

                if (!handStack.isEmpty() && handStack.getItem() != null && handStack.getItem() instanceof ItemBow && Bows.getValue()) {
                    doSpoofs();
                    if (debug.getValue()) Command.sendMessage("trying to spoof");
                }
            }

        } else if (event.getPacket() instanceof CPacketPlayerTryUseItem) {
            CPacketPlayerTryUseItem packet2 = (CPacketPlayerTryUseItem) event.getPacket();

            if (packet2.getHand() == EnumHand.MAIN_HAND) {
                ItemStack handStack = mc.player.getHeldItem(EnumHand.MAIN_HAND);

                if (!handStack.isEmpty() && handStack.getItem() != null) {
                    if (handStack.getItem() instanceof ItemEgg && eggs.getValue()) {
                        doSpoofs();
                    } else if (handStack.getItem() instanceof ItemEnderPearl && pearls.getValue()) {
                        doSpoofs();
                    } else if (handStack.getItem() instanceof ItemSnowball && snowballs.getValue()) {
                        doSpoofs();
                    }
                }
            }
        }
    }

    private void doSpoofs() {
        if (System.currentTimeMillis() - lastShootTime >= Timeout.getValue()) {
            shooting = true;
            lastShootTime = System.currentTimeMillis();

            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));

            for (int index = 0; index < spoofs.getValue(); ++index) {
                if (bypass.getValue()) {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1e-10, mc.player.posZ, false));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1e-10, mc.player.posZ, true));
                } else {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1e-10, mc.player.posZ, true));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1e-10, mc.player.posZ, false));
                }

            }

            if (debug.getValue()) Command.sendMessage("Spoofed");

            shooting = false;
        }
    }
}