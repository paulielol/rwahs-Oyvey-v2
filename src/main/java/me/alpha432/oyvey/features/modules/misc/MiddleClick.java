package me.alpha432.oyvey.features.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.player.MCP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;


import me.alpha432.oyvey.util.*;

import me.alpha432.oyvey.features.setting.Setting;


public class MiddleClick extends Module {

    public static MiddleClick INSTANCE;
    public Setting<Boolean> friend = this.register(new Setting<Boolean>("Friend", false));
    public Setting<Boolean> pearl = this.register(new Setting<Boolean>("Pearl", false));
    public Setting<Boolean> xp = this.register(new Setting<Boolean>("EXP", false));
    public Setting<Boolean> offhandSwap = this.register(new Setting<Boolean>("OffhandSwap", false));
    boolean hasPressed = false;

    public MiddleClick() {
        super("Middle Click", "Middle Click", Category.MISC, false, false, false);

        MiddleClick.INSTANCE = this;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (NullUtils.nullCheck()) {
            return;
        }
        Entity entity = null;
        if (Mouse.isButtonDown(2)) {
            int oldSlot;
            Entity pointed = MiddleClick.mc.getRenderManager().pointedEntity;
            if (Mouse.isButtonDown(2)) {
                if (this.friend.getValue().booleanValue() && pointed != null) {
                    if (OyVey.friendManager.isFriend(entity.getName())) {
                        OyVey.friendManager.removeFriend(entity.getName());
                        Command.sendMessage(ChatFormatting.RED + entity.getName() + ChatFormatting.RED + " has been unfriended.");
                    } else {
                        OyVey.friendManager.addFriend(entity.getName());
                        Command.sendMessage(ChatFormatting.AQUA + entity.getName() + ChatFormatting.AQUA + " has been friended.");
                    }
                }
                if (pointed == null && this.pearl.getValue().booleanValue() && this.allowPearl()) {
                    oldSlot = MiddleClick.mc.player.inventory.currentItem;
                    int pearlSlot = InventoryUtils.getHotbarItemSlot(Items.ENDER_PEARL);
                    if (pearlSlot == -1 && !this.offhandSwap.getValue().booleanValue()) {
                        Command.sendMessage(ChatFormatting.AQUA + "No pearls In Hotbar");
                        this.hasPressed = true;
                        return;
                    }
                    Item oldItem = MiddleClick.mc.player.getHeldItemOffhand().getItem();
                    if (this.offhandSwap.getValue().booleanValue()) {
                        InventoryUtils.moveItemToOffhand(Items.ENDER_PEARL);
                    } else {
                        InventoryUtils.switchToSlotGhost(pearlSlot);
                    }
                    MiddleClick.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(this.offhandSwap.getValue() != false ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
                    if (this.offhandSwap.getValue().booleanValue()) {
                        InventoryUtils.moveItemToOffhand(oldItem);
                    } else {
                        InventoryUtils.switchToSlotGhost(oldSlot);
                    }
                }
            }
            if (this.xp.getValue().booleanValue() && this.allowExp()) {
                oldSlot = MiddleClick.mc.player.inventory.currentItem;
                int xpSlot = InventoryUtils.getHotbarItemSlot(Items.EXPERIENCE_BOTTLE);
                if (xpSlot == -1) {
                    this.hasPressed = true;
                    return;
                }
                InventoryUtils.switchToSlotGhost(xpSlot);
                MiddleClick.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                InventoryUtils.switchToSlotGhost(oldSlot);
            }
            this.hasPressed = true;
        } else {
            this.hasPressed = false;
        }
    }

    boolean allowPearl() {
        RayTraceResult mouseOver = MiddleClick.mc.objectMouseOver;
        return mouseOver == null || mouseOver.typeOfHit == RayTraceResult.Type.MISS;
    }

    boolean allowExp() {
        RayTraceResult mouseOver = MiddleClick.mc.objectMouseOver;
        return mouseOver != null && mouseOver.typeOfHit == RayTraceResult.Type.BLOCK;
    }
}


