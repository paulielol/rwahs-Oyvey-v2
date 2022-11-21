package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import me.alpha432.oyvey.util.NullUtils;
import net.minecraft.init.Blocks;
import me.alpha432.oyvey.util.InventoryUtils;
import me.alpha432.oyvey.util.BlockUtils;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import me.alpha432.oyvey.util.RenderUtils;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

public class NewSurround
        extends Module {
    private final Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 0, 0, 1000));
    private final Setting<Integer> blocksPerTick = this.register(new Setting<Integer>("blocksPerTick", 20, 1, 50));
    private final Setting<Integer> retryAmount = this.register(new Setting<Integer>("Retry", 20, 1, 50));
    private final Setting<Boolean> jumpDisable = this.register(new Setting<Boolean>("JumpDisable", false));
    private final Setting<Boolean> predict = this.register(new Setting<Boolean>("Predict", true));
    private final Setting<Boolean> dynamic = this.register(new Setting<Boolean>("Dynamic", true));
    private final Setting<Boolean> antiPhase = this.register(new Setting<Boolean>("AntiPhase", true));
    private final Timer timer = new Timer();
    double startY = 0.0;
    List<BlockPos> activeBlocks = new ArrayList<BlockPos>();
    boolean shouldPredict = false;

    public NewSurround() {
        super("NewSurround", "NewSurrounds you with Obsidian", Module.Category.COMBAT, true, false, false);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (NullUtils.nullCheck()) {
            return;
        }
        this.shouldPredict = true;
        if (this.jumpDisable.getValue().booleanValue() && (!NewSurround.mc.player.onGround || NewSurround.mc.player.posY != this.startY)) {
            this.setEnabled(false);
            return;
        }
        if (this.timer.isPassed()) {
            this.activeBlocks.clear();
            boolean switched = false;
            int oldSlot = NewSurround.mc.player.inventory.currentItem;
            int blockSlot = this.getSlot();
            if (blockSlot == -1) {
                this.setEnabled(false);
                return;
            }
            int blocksInTick = 0;
            block0: for (int i = 0; i < this.retryAmount.getValue().intValue(); ++i) {
                for (BlockPos pos : this.getOffsets()) {
                    if (blocksInTick > this.blocksPerTick.getValue().intValue()) continue block0;
                    if (!this.canPlaceBlock(pos)) continue;
                    this.activeBlocks.add(pos);
                    if (!switched) {
                        InventoryUtils.switchToSlotGhost(blockSlot);
                        switched = true;
                    }
                    BlockUtils.placeBlock(pos, true);
                    ++blocksInTick;
                }
            }
            if (switched) {
                InventoryUtils.switchToSlotGhost(oldSlot);
            }
            this.timer.resetDelay();
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof SPacketBlockChange && this.predict.getValue().booleanValue()) {
            SPacketBlockChange packet = (SPacketBlockChange)event.getPacket();
            for (BlockPos pos : this.getOffsets()) {
                if (!this.shouldPredict || !pos.equals((Object)packet.getBlockPosition()) || packet.getBlockState().getBlock() != Blocks.AIR) continue;
                int oldSlot = NewSurround.mc.player.inventory.currentItem;
                int blockSlot = this.getSlot();
                if (blockSlot == -1) {
                    return;
                }
                InventoryUtils.switchToSlotGhost(blockSlot);
                BlockUtils.placeBlock(pos, true);
                InventoryUtils.switchToSlotGhost(oldSlot);
                this.shouldPredict = false;
                break;
            }
        }
    }


    @Override
    public void onEnable() {
        if (NullUtils.nullCheck()) {
            return;
        }
        super.onEnable();
        this.startY = NewSurround.mc.player.posY;
    }

    int getSlot() {
        int slot = -1;
        slot = InventoryUtils.getHotbarItemSlot(Item.getItemFromBlock((Block)Blocks.OBSIDIAN));
        if (slot == -1) {
            slot = InventoryUtils.getHotbarItemSlot(Item.getItemFromBlock((Block)Blocks.ENDER_CHEST));
        }
        return slot;
    }

    boolean canPlaceBlock(BlockPos pos) {
        boolean allow = true;
        if (!NewSurround.mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            allow = false;
        }
        for (Entity entity : NewSurround.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (!(entity instanceof EntityPlayer)) continue;
            allow = false;
            break;
        }
        return allow;
    }

    List<BlockPos> getOffsets() {
        BlockPos playerPos = this.getPlayerPos();
        ArrayList<BlockPos> offsets = new ArrayList<BlockPos>();
        if (this.dynamic.getValue().booleanValue()) {
            int z;
            int x;
            double decimalX = Math.abs(NewSurround.mc.player.posX) - Math.floor(Math.abs(NewSurround.mc.player.posX));
            double decimalZ = Math.abs(NewSurround.mc.player.posZ) - Math.floor(Math.abs(NewSurround.mc.player.posZ));
            int offX = this.calcOffset(decimalX);
            int offZ = this.calcOffset(decimalZ);
            int lengthXPos = this.calcLength(decimalX, false);
            int lengthXNeg = this.calcLength(decimalX, true);
            int lengthZPos = this.calcLength(decimalZ, false);
            int lengthZNeg = this.calcLength(decimalZ, true);
            ArrayList<BlockPos> tempOffsets = new ArrayList<BlockPos>();
            offsets.addAll(this.getOverlapPos());
            for (x = 1; x < lengthXPos + 1; ++x) {
                tempOffsets.add(this.addToPlayer(playerPos, x, 0.0, 1 + lengthZPos));
                tempOffsets.add(this.addToPlayer(playerPos, x, 0.0, -(1 + lengthZNeg)));
            }
            for (x = 0; x <= lengthXNeg; ++x) {
                tempOffsets.add(this.addToPlayer(playerPos, -x, 0.0, 1 + lengthZPos));
                tempOffsets.add(this.addToPlayer(playerPos, -x, 0.0, -(1 + lengthZNeg)));
            }
            for (z = 1; z < lengthZPos + 1; ++z) {
                tempOffsets.add(this.addToPlayer(playerPos, 1 + lengthXPos, 0.0, z));
                tempOffsets.add(this.addToPlayer(playerPos, -(1 + lengthXNeg), 0.0, z));
            }
            for (z = 0; z <= lengthZNeg; ++z) {
                tempOffsets.add(this.addToPlayer(playerPos, 1 + lengthXPos, 0.0, -z));
                tempOffsets.add(this.addToPlayer(playerPos, -(1 + lengthXNeg), 0.0, -z));
            }
            for (BlockPos pos : tempOffsets) {
                offsets.add(pos.add(0, -1, 0));
                offsets.add(pos);
            }
        } else {
            offsets.add(playerPos.add(0, -1, 0));
            for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                offsets.add(playerPos.add(facing.getXOffset(), -1, facing.getZOffset()));
                offsets.add(playerPos.add(facing.getXOffset(), 0, facing.getZOffset()));
            }
        }
        return offsets;
    }

    BlockPos addToPlayer(BlockPos playerPos, double x, double y, double z) {
        if (playerPos.getX() < 0) {
            x = -x;
        }
        if (playerPos.getY() < 0) {
            y = -y;
        }
        if (playerPos.getZ() < 0) {
            z = -z;
        }
        return playerPos.add(x, y, z);
    }

    int calcLength(double decimal, boolean negative) {
        if (negative) {
            return decimal <= 0.3 ? 1 : 0;
        }
        return decimal >= 0.7 ? 1 : 0;
    }

    boolean isOverlapping(int offsetX, int offsetZ) {
        boolean overlapping = false;
        double decimalX = NewSurround.mc.player.posX - Math.floor(NewSurround.mc.player.posX);
        decimalX = Math.abs(decimalX);
        double decimalZ = NewSurround.mc.player.posZ - Math.floor(NewSurround.mc.player.posZ);
        decimalZ = Math.abs(decimalZ);
        if (offsetX > 0 && decimalX > 0.7) {
            overlapping = true;
        }
        if (offsetX < 0 && decimalX < 0.3) {
            overlapping = true;
        }
        if (offsetZ > 0 && decimalZ >= 0.7) {
            overlapping = true;
        }
        if (offsetZ < 0 && decimalZ < 0.3) {
            overlapping = true;
        }
        return overlapping;
    }

    List<BlockPos> getOverlapPos() {
        ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
        double decimalX = NewSurround.mc.player.posX - Math.floor(NewSurround.mc.player.posX);
        double decimalZ = NewSurround.mc.player.posZ - Math.floor(NewSurround.mc.player.posZ);
        int offX = this.calcOffset(decimalX);
        int offZ = this.calcOffset(decimalZ);
        positions.add(this.getPlayerPos());
        for (int x = 0; x <= Math.abs(offX); ++x) {
            for (int z = 0; z <= Math.abs(offZ); ++z) {
                int properX = x * offX;
                int properZ = z * offZ;
                positions.add(this.getPlayerPos().add(properX, -1, properZ));
            }
        }
        return positions;
    }

    int calcOffset(double dec) {
        return dec >= 0.7 ? 1 : (dec <= 0.3 ? -1 : 0);
    }

    BlockPos getPlayerPos() {
        double decimalPoint = NewSurround.mc.player.posY - Math.floor(NewSurround.mc.player.posY);
        return new BlockPos(NewSurround.mc.player.posX, decimalPoint > 0.8 ? Math.floor(NewSurround.mc.player.posY) + 1.0 : Math.floor(NewSurround.mc.player.posY), NewSurround.mc.player.posZ);
    }
}