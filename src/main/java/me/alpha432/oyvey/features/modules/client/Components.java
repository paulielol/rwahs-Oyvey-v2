package me.alpha432.oyvey.features.modules.client;

import me.alpha432.oyvey.features.modules.*;
import me.alpha432.oyvey.features.setting.*;
import me.alpha432.oyvey.event.events.*;
import net.minecraft.entity.player.*;
import me.alpha432.oyvey.*;
import net.minecraft.entity.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.init.*;
import net.minecraft.block.*;
import net.minecraft.client.renderer.*;
import me.alpha432.oyvey.util.*;
import net.minecraft.util.math.*;
import net.minecraft.item.*;
import net.minecraft.util.*;

public class Components extends Module {
    private static final ResourceLocation box;
    private static final double HALF_PI = 1.5707963267948966;
    public Setting<Boolean> inventory;
    public Setting<Integer> invX;
    public Setting<Integer> invY;
    public Setting<Integer> fineinvX;
    public Setting<Integer> fineinvY;
    public Setting<Integer> invH;
    public Setting<Boolean> holeHud;
    public Setting<Integer> holeX;
    public Setting<Integer> holeY;
    public Setting<Integer> scale;
    public Setting<Boolean> playerViewer;
    public Setting<Integer> playerViewerX;
    public Setting<Integer> playerViewerY;
    public Setting<Float> playerScale;

    public Components() {
        super("Components", "HUD+", Module.Category.CLIENT, false, false, true);
        this.inventory = (Setting<Boolean>) this.register(new Setting("Inventory", (Object) false));
        this.invX = (Setting<Integer>) this.register(new Setting("InvX", (Object) 564, (Object) 0, (Object) 1000, v -> (boolean) this.inventory.getValue()));
        this.invY = (Setting<Integer>) this.register(new Setting("InvY", (Object) 467, (Object) 0, (Object) 1000, v -> (boolean) this.inventory.getValue()));
        this.fineinvX = (Setting<Integer>) this.register(new Setting("InvFineX", (Object) 0, v -> (boolean) this.inventory.getValue()));
        this.fineinvY = (Setting<Integer>) this.register(new Setting("InvFineY", (Object) 0, v -> (boolean) this.inventory.getValue()));
        this.invH = (Setting<Integer>) this.register(new Setting("InvH", (Object) 3, v -> (boolean) this.inventory.getValue()));
        this.holeHud = (Setting<Boolean>) this.register(new Setting("HoleHUD", (Object) false));
        this.holeX = (Setting<Integer>) this.register(new Setting("HoleX", (Object) 279, (Object) 0, (Object) 1000, v -> (boolean) this.holeHud.getValue()));
        this.holeY = (Setting<Integer>) this.register(new Setting("HoleY", (Object) 485, (Object) 0, (Object) 1000, v -> (boolean) this.holeHud.getValue()));
        this.playerViewer = (Setting<Boolean>) this.register(new Setting("PlayerViewer", (Object) false));
        this.playerViewerX = (Setting<Integer>) this.register(new Setting("PlayerX", (Object) 752, (Object) 0, (Object) 1000, v -> (boolean) this.playerViewer.getValue()));
        this.playerViewerY = (Setting<Integer>) this.register(new Setting("PlayerY", (Object) 497, (Object) 0, (Object) 1000, v -> (boolean) this.playerViewer.getValue()));
        this.playerScale = (Setting<Float>) this.register(new Setting("PlayerScale", (Object) 1.0f, (Object) 0.1f, (Object) 2.0f, v -> (boolean) this.playerViewer.getValue()));
    }

    public void onRender2D(final Render2DEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (this.playerViewer.getValue()) {
            this.drawPlayer();
        }
        if (this.holeHud.getValue()) {
            this.drawOverlay(event.partialTicks);
        }
        if (this.inventory.getValue()) {
            this.renderInventory();
        }
    }

    public static EntityPlayer getClosestEnemy() {
        EntityPlayer closestPlayer = null;
        for (final EntityPlayer player : Components.mc.world.playerEntities) {
            if (player == Components.mc.player) {
                continue;
            }
            if (OyVey.friendManager.isFriend(player)) {
                continue;
            }
            if (closestPlayer == null) {
                closestPlayer = player;
            } else {
                if (Components.mc.player.getDistanceSq((Entity) player) >= Components.mc.player.getDistanceSq((Entity) closestPlayer)) {
                    continue;
                }
                closestPlayer = player;
            }
        }
        return closestPlayer;
    }

    public void drawPlayer(final EntityPlayer player, final int x, final int y) {
        final EntityPlayer ent = player;
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.rotate(0.0f, 0.0f, 5.0f, 0.0f);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) ((int) this.playerViewerX.getValue() + 25), (float) ((int) this.playerViewerY.getValue() + 25), 50.0f);
        GlStateManager.scale(-50.0f * (float) this.playerScale.getValue(), 50.0f * (float) this.playerScale.getValue(), 50.0f * (float) this.playerScale.getValue());
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-(float) Math.atan((int) this.playerViewerY.getValue() / 40.0f) * 20.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        final RenderManager rendermanager = Components.mc.getRenderManager();
        rendermanager.setPlayerViewY(180.0f);
        rendermanager.setRenderShadow(false);
        try {
            rendermanager.renderEntity((Entity) ent, 0.0, 0.0, 0.0, 0.0f, 1.0f, false);
        } catch (Exception ex) {
        }
        rendermanager.setRenderShadow(true);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.depthFunc(515);
        GlStateManager.resetColor();
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();
    }

    public void drawPlayer() {
        final EntityPlayer ent = (EntityPlayer) Components.mc.player;
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.rotate(0.0f, 0.0f, 5.0f, 0.0f);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) ((int) this.playerViewerX.getValue() + 25), (float) ((int) this.playerViewerY.getValue() + 25), 50.0f);
        GlStateManager.scale(-50.0f * (float) this.playerScale.getValue(), 50.0f * (float) this.playerScale.getValue(), 50.0f * (float) this.playerScale.getValue());
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-(float) Math.atan((int) this.playerViewerY.getValue() / 40.0f) * 20.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        final RenderManager rendermanager = Components.mc.getRenderManager();
        rendermanager.setPlayerViewY(180.0f);
        rendermanager.setRenderShadow(false);
        try {
            rendermanager.renderEntity((Entity) ent, 0.0, 0.0, 0.0, 0.0f, 1.0f, false);
        } catch (Exception ex) {
        }
        rendermanager.setRenderShadow(true);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.depthFunc(515);
        GlStateManager.resetColor();
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();
    }

    private double getX(final double rad) {
        return Math.sin(rad) * ((int) this.scale.getValue() * 10);
    }

    private double getY(final double rad) {
        final double epicPitch = MathHelper.clamp(Components.mc.player.rotationPitch + 30.0f, -90.0f, 90.0f);
        final double pitchRadians = Math.toRadians(epicPitch);
        return Math.cos(rad) * Math.sin(pitchRadians) * ((int) this.scale.getValue() * 10);
    }

    public void drawOverlay(final float partialTicks) {
        float yaw = 0.0f;
        final int dir = MathHelper.floor(Components.mc.player.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3;
        switch (dir) {
            case 1: {
                yaw = 90.0f;
                break;
            }
            case 2: {
                yaw = -180.0f;
                break;
            }
            case 3: {
                yaw = -90.0f;
                break;
            }
        }
        final BlockPos northPos = this.traceToBlock(partialTicks, yaw);
        final Block north = this.getBlock(northPos);
        if (north != null && north != Blocks.AIR) {
            final int damage = this.getBlockDamage(northPos);
            if (damage != 0) {
                RenderUtil.drawRect((float) ((int) this.holeX.getValue() + 16), (float) (int) this.holeY.getValue(), (float) ((int) this.holeX.getValue() + 32), (float) ((int) this.holeY.getValue() + 16), 1627324416);
            }
            this.drawBlock(north, (float) ((int) this.holeX.getValue() + 16), (float) (int) this.holeY.getValue());
        }
        final BlockPos southPos = this.traceToBlock(partialTicks, yaw - 180.0f);
        final Block south = this.getBlock(southPos);
        if (south != null && south != Blocks.AIR) {
            final int damage2 = this.getBlockDamage(southPos);
            if (damage2 != 0) {
                RenderUtil.drawRect((float) ((int) this.holeX.getValue() + 16), (float) ((int) this.holeY.getValue() + 32), (float) ((int) this.holeX.getValue() + 32), (float) ((int) this.holeY.getValue() + 48), 1627324416);
            }
            this.drawBlock(south, (float) ((int) this.holeX.getValue() + 16), (float) ((int) this.holeY.getValue() + 32));
        }
        final BlockPos eastPos = this.traceToBlock(partialTicks, yaw + 90.0f);
        final Block east = this.getBlock(eastPos);
        if (east != null && east != Blocks.AIR) {
            final int damage3 = this.getBlockDamage(eastPos);
            if (damage3 != 0) {
                RenderUtil.drawRect((float) ((int) this.holeX.getValue() + 32), (float) ((int) this.holeY.getValue() + 16), (float) ((int) this.holeX.getValue() + 48), (float) ((int) this.holeY.getValue() + 32), 1627324416);
            }
            this.drawBlock(east, (float) ((int) this.holeX.getValue() + 32), (float) ((int) this.holeY.getValue() + 16));
        }
        final BlockPos westPos = this.traceToBlock(partialTicks, yaw - 90.0f);
        final Block west = this.getBlock(westPos);
        if (west != null && west != Blocks.AIR) {
            final int damage4 = this.getBlockDamage(westPos);
            if (damage4 != 0) {
                RenderUtil.drawRect((float) (int) this.holeX.getValue(), (float) ((int) this.holeY.getValue() + 16), (float) ((int) this.holeX.getValue() + 16), (float) ((int) this.holeY.getValue() + 32), 1627324416);
            }
            this.drawBlock(west, (float) (int) this.holeX.getValue(), (float) ((int) this.holeY.getValue() + 16));
        }
    }

    public void drawOverlay(final float partialTicks, final Entity player, final int x, final int y) {
        float yaw = 0.0f;
        final int dir = MathHelper.floor(player.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3;
        switch (dir) {
            case 1: {
                yaw = 90.0f;
                break;
            }
            case 2: {
                yaw = -180.0f;
                break;
            }
            case 3: {
                yaw = -90.0f;
                break;
            }
        }
        final BlockPos northPos = this.traceToBlock(partialTicks, yaw, player);
        final Block north = this.getBlock(northPos);
        if (north != null && north != Blocks.AIR) {
            final int damage = this.getBlockDamage(northPos);
            if (damage != 0) {
                RenderUtil.drawRect((float) (x + 16), (float) y, (float) (x + 32), (float) (y + 16), 1627324416);
            }
            this.drawBlock(north, (float) (x + 16), (float) y);
        }
        final BlockPos southPos = this.traceToBlock(partialTicks, yaw - 180.0f, player);
        final Block south = this.getBlock(southPos);
        if (south != null && south != Blocks.AIR) {
            final int damage2 = this.getBlockDamage(southPos);
            if (damage2 != 0) {
                RenderUtil.drawRect((float) (x + 16), (float) (y + 32), (float) (x + 32), (float) (y + 48), 1627324416);
            }
            this.drawBlock(south, (float) (x + 16), (float) (y + 32));
        }
        final BlockPos eastPos = this.traceToBlock(partialTicks, yaw + 90.0f, player);
        final Block east = this.getBlock(eastPos);
        if (east != null && east != Blocks.AIR) {
            final int damage3 = this.getBlockDamage(eastPos);
            if (damage3 != 0) {
                RenderUtil.drawRect((float) (x + 32), (float) (y + 16), (float) (x + 48), (float) (y + 32), 1627324416);
            }
            this.drawBlock(east, (float) (x + 32), (float) (y + 16));
        }
        final BlockPos westPos = this.traceToBlock(partialTicks, yaw - 90.0f, player);
        final Block west = this.getBlock(westPos);
        if (west != null && west != Blocks.AIR) {
            final int damage4 = this.getBlockDamage(westPos);
            if (damage4 != 0) {
                RenderUtil.drawRect((float) x, (float) (y + 16), (float) (x + 16), (float) (y + 32), 1627324416);
            }
            this.drawBlock(west, (float) x, (float) (y + 16));
        }
    }

    private int getBlockDamage(final BlockPos pos) {
        for (final DestroyBlockProgress destBlockProgress : Components.mc.renderGlobal.damagedBlocks.values()) {
            if (destBlockProgress.getPosition().getX() == pos.getX() && destBlockProgress.getPosition().getY() == pos.getY() && destBlockProgress.getPosition().getZ() == pos.getZ()) {
                return destBlockProgress.getPartialBlockDamage();
            }
        }
        return 0;
    }

    private BlockPos traceToBlock(final float partialTicks, final float yaw) {
        final Vec3d pos = EntityUtil.interpolateEntity((Entity) Components.mc.player, partialTicks);
        final Vec3d dir = MathUtil.direction(yaw);
        return new BlockPos(pos.x + dir.x, pos.y, pos.z + dir.z);
    }

    private BlockPos traceToBlock(final float partialTicks, final float yaw, final Entity player) {
        final Vec3d pos = EntityUtil.interpolateEntity(player, partialTicks);
        final Vec3d dir = MathUtil.direction(yaw);
        return new BlockPos(pos.x + dir.x, pos.y, pos.z + dir.z);
    }

    private Block getBlock(final BlockPos pos) {
        final Block block = Components.mc.world.getBlockState(pos).getBlock();
        if (block == Blocks.BEDROCK || block == Blocks.OBSIDIAN) {
            return block;
        }
        return Blocks.AIR;
    }

    private void drawBlock(final Block block, final float x, final float y) {
        final ItemStack stack = new ItemStack(block);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.translate(x, y, 0.0f);
        Components.mc.getRenderItem().zLevel = 501.0f;
        Components.mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
        Components.mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }

    public void renderInventory() {
        this.boxrender((int) this.invX.getValue() + (int) this.fineinvX.getValue(), (int) this.invY.getValue() + (int) this.fineinvY.getValue());
        this.itemrender((NonNullList<ItemStack>) Components.mc.player.inventory.mainInventory, (int) this.invX.getValue() + (int) this.fineinvX.getValue(), (int) this.invY.getValue() + (int) this.fineinvY.getValue());
    }

    private static void preboxrender() {
        GL11.glPushMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.disableAlpha();
        GlStateManager.clear(256);
        GlStateManager.enableBlend();
        GlStateManager.color(255.0f, 255.0f, 255.0f, 255.0f);
    }

    private static void postboxrender() {
        GlStateManager.disableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
        GL11.glPopMatrix();
    }

    private static void preitemrender() {
        GL11.glPushMatrix();
        GL11.glDepthMask(true);
        GlStateManager.clear(256);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.scale(1.0f, 1.0f, 0.01f);
    }

    private static void postitemrender() {
        GlStateManager.scale(1.0f, 1.0f, 1.0f);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GL11.glPopMatrix();
    }

    private void boxrender(final int x, final int y) {
        preboxrender();
        Components.mc.renderEngine.bindTexture(Components.box);
        RenderUtil.drawTexturedRect(x, y, 0, 0, 176, 16, 500);
        RenderUtil.drawTexturedRect(x, y + 16, 0, 16, 176, 54 + (int) this.invH.getValue(), 500);
        RenderUtil.drawTexturedRect(x, y + 16 + 54, 0, 160, 176, 8, 500);
        postboxrender();
    }

    private void itemrender(final NonNullList<ItemStack> items, final int x, final int y) {
        for (int i = 0; i < items.size() - 9; ++i) {
            final int iX = x + i % 9 * 18 + 8;
            final int iY = y + i / 9 * 18 + 18;
            final ItemStack itemStack = (ItemStack) items.get(i + 9);
            preitemrender();
            Components.mc.getRenderItem().zLevel = 501.0f;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(itemStack, iX, iY);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(Components.mc.fontRenderer, itemStack, iX, iY, (String) null);
            Components.mc.getRenderItem().zLevel = 0.0f;
            postitemrender();
        }
    }

    public static void drawCompleteImage(final int posX, final int posY, final int width, final int height) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)posX, (float)posY, 0.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, (float)height, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f((float)width, (float)height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f((float)width, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    static {
        box = new ResourceLocation("textures/gui/container/shulker_box.png");
    }
}