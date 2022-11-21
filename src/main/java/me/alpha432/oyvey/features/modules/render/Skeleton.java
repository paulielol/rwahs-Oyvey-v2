//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.model.ModelPlayer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  org.lwjgl.opengl.GL11
 */
package me.alpha432.oyvey.features.modules.render;

import java.util.HashMap;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.Render3DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.BlockUtil;
import me.alpha432.oyvey.util.ColorUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class Skeleton
        extends Module {
    private static final HashMap<EntityPlayer, float[][]> entities = new HashMap();
    private final Setting<Float> lineWidth = this.register(new Setting<Float>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f)));
    private final Setting<Boolean> invisibles = this.register(new Setting<Boolean>("Invisibles", false));
    private final Setting<Integer> alpha = this.register(new Setting<Integer>("Alpha", 255, 0, 255));

    public Skeleton() {
        super("Skeleton", "Draws a skeleton inside the player.", Module.Category.RENDER, false, false, false);
    }

    public static void addEntity(EntityPlayer e, ModelPlayer model) {
        entities.put(e, new float[][]{{model.bipedHead.rotateAngleX, model.bipedHead.rotateAngleY, model.bipedHead.rotateAngleZ}, {model.bipedRightArm.rotateAngleX, model.bipedRightArm.rotateAngleY, model.bipedRightArm.rotateAngleZ}, {model.bipedLeftArm.rotateAngleX, model.bipedLeftArm.rotateAngleY, model.bipedLeftArm.rotateAngleZ}, {model.bipedRightLeg.rotateAngleX, model.bipedRightLeg.rotateAngleY, model.bipedRightLeg.rotateAngleZ}, {model.bipedLeftLeg.rotateAngleX, model.bipedLeftLeg.rotateAngleY, model.bipedLeftLeg.rotateAngleZ}});
    }

    private Vec3d getVec3(Render3DEvent event, EntityPlayer e) {
        float pt = event.getPartialTicks();
        double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double)pt;
        double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double)pt;
        double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double)pt;
        return new Vec3d(x, y, z);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (Skeleton.fullNullCheck()) {
            return;
        }
        this.startEnd(true);
        GL11.glEnable((int)2903);
        GL11.glDisable((int)2848);
        entities.keySet().removeIf(this::doesntContain);
        Skeleton.mc.world.playerEntities.forEach(e -> this.drawSkeleton(event, (EntityPlayer)e));
        Gui.drawRect((int)0, (int)0, (int)0, (int)0, (int)0);
        this.startEnd(false);
    }

    private void drawSkeleton(Render3DEvent event, EntityPlayer e) {
        if (!BlockUtil.isPosInFov(new BlockPos(e.posX, e.posY, e.posZ)).booleanValue()) {
            return;
        }
        if (e.isInvisible() && !this.invisibles.getValue().booleanValue()) {
            return;
        }
        float[][] entPos = entities.get((Object)e);
        if (entPos != null && e.isEntityAlive() && !e.isDead && e != Skeleton.mc.player && !e.isPlayerSleeping()) {
            GL11.glPushMatrix();
            GL11.glEnable((int)2848);
            GL11.glLineWidth((float)this.lineWidth.getValue().floatValue());
            if (OyVey.friendManager.isFriend(e.getName())) {
                GlStateManager.color((float)0.0f, (float)191.0f, (float)230.0f, (float)this.alpha.getValue().intValue());
            } else {
                GlStateManager.color((float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRed() / 255.0f : (float)ClickGui.getInstance().red.getValue().intValue() / 255.0f), (float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getGreen() / 255.0f : (float)ClickGui.getInstance().green.getValue().intValue() / 255.0f), (float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getBlue() / 255.0f : (float)ClickGui.getInstance().blue.getValue().intValue() / 255.0f), (float)((float)this.alpha.getValue().intValue() / 255.0f));
            }
            Vec3d vec = this.getVec3(event, e);
            double x = vec.x - Skeleton.mc.getRenderManager().renderPosX;
            double y = vec.y - Skeleton.mc.getRenderManager().renderPosY;
            double z = vec.z - Skeleton.mc.getRenderManager().renderPosZ;
            GL11.glTranslated((double)x, (double)y, (double)z);
            float xOff = e.prevRenderYawOffset + (e.renderYawOffset - e.prevRenderYawOffset) * event.getPartialTicks();
            GL11.glRotatef((float)(-xOff), (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glTranslated((double)0.0, (double)0.0, (double)(e.isSneaking() ? -0.235 : 0.0));
            float yOff = e.isSneaking() ? 0.6f : 0.75f;
            GL11.glPushMatrix();
            GL11.glTranslated((double)-0.125, (double)yOff, (double)0.0);
            if (entPos[3][0] != 0.0f) {
                GL11.glRotatef((float)(entPos[3][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
            }
            if (entPos[3][1] != 0.0f) {
                GL11.glRotatef((float)(entPos[3][1] * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
            }
            if (entPos[3][2] != 0.0f) {
                GL11.glRotatef((float)(entPos[3][2] * 57.295776f), (float)0.0f, (float)0.0f, (float)1.0f);
            }
            GL11.glBegin((int)3);
            GL11.glVertex3d((double)0.0, (double)0.0, (double)0.0);
            GL11.glVertex3d((double)0.0, (double)(-yOff), (double)0.0);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated((double)0.125, (double)yOff, (double)0.0);
            if (entPos[4][0] != 0.0f) {
                GL11.glRotatef((float)(entPos[4][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
            }
            if (entPos[4][1] != 0.0f) {
                GL11.glRotatef((float)(entPos[4][1] * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
            }
            if (entPos[4][2] != 0.0f) {
                GL11.glRotatef((float)(entPos[4][2] * 57.295776f), (float)0.0f, (float)0.0f, (float)1.0f);
            }
            GL11.glBegin((int)3);
            GL11.glVertex3d((double)0.0, (double)0.0, (double)0.0);
            GL11.glVertex3d((double)0.0, (double)(-yOff), (double)0.0);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glTranslated((double)0.0, (double)0.0, (double)(e.isSneaking() ? 0.25 : 0.0));
            GL11.glPushMatrix();
            GL11.glTranslated((double)0.0, (double)(e.isSneaking() ? -0.05 : 0.0), (double)(e.isSneaking() ? -0.01725 : 0.0));
            GL11.glPushMatrix();
            GL11.glTranslated((double)-0.375, (double)((double)yOff + 0.55), (double)0.0);
            if (entPos[1][0] != 0.0f) {
                GL11.glRotatef((float)(entPos[1][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
            }
            if (entPos[1][1] != 0.0f) {
                GL11.glRotatef((float)(entPos[1][1] * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
            }
            if (entPos[1][2] != 0.0f) {
                GL11.glRotatef((float)(-entPos[1][2] * 57.295776f), (float)0.0f, (float)0.0f, (float)1.0f);
            }
            GL11.glBegin((int)3);
            GL11.glVertex3d((double)0.0, (double)0.0, (double)0.0);
            GL11.glVertex3d((double)0.0, (double)-0.5, (double)0.0);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated((double)0.375, (double)((double)yOff + 0.55), (double)0.0);
            if (entPos[2][0] != 0.0f) {
                GL11.glRotatef((float)(entPos[2][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
            }
            if (entPos[2][1] != 0.0f) {
                GL11.glRotatef((float)(entPos[2][1] * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
            }
            if (entPos[2][2] != 0.0f) {
                GL11.glRotatef((float)(-entPos[2][2] * 57.295776f), (float)0.0f, (float)0.0f, (float)1.0f);
            }
            GL11.glBegin((int)3);
            GL11.glVertex3d((double)0.0, (double)0.0, (double)0.0);
            GL11.glVertex3d((double)0.0, (double)-0.5, (double)0.0);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glRotatef((float)(xOff - e.rotationYawHead), (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glPushMatrix();
            GL11.glTranslated((double)0.0, (double)((double)yOff + 0.55), (double)0.0);
            if (entPos[0][0] != 0.0f) {
                GL11.glRotatef((float)(entPos[0][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
            }
            GL11.glBegin((int)3);
            GL11.glVertex3d((double)0.0, (double)0.0, (double)0.0);
            GL11.glVertex3d((double)0.0, (double)0.3, (double)0.0);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPopMatrix();
            GL11.glRotatef((float)(e.isSneaking() ? 25.0f : 0.0f), (float)1.0f, (float)0.0f, (float)0.0f);
            GL11.glTranslated((double)0.0, (double)(e.isSneaking() ? -0.16175 : 0.0), (double)(e.isSneaking() ? -0.48025 : 0.0));
            GL11.glPushMatrix();
            GL11.glTranslated((double)0.0, (double)yOff, (double)0.0);
            GL11.glBegin((int)3);
            GL11.glVertex3d((double)-0.125, (double)0.0, (double)0.0);
            GL11.glVertex3d((double)0.125, (double)0.0, (double)0.0);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated((double)0.0, (double)yOff, (double)0.0);
            GL11.glBegin((int)3);
            GL11.glVertex3d((double)0.0, (double)0.0, (double)0.0);
            GL11.glVertex3d((double)0.0, (double)0.55, (double)0.0);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated((double)0.0, (double)((double)yOff + 0.55), (double)0.0);
            GL11.glBegin((int)3);
            GL11.glVertex3d((double)-0.375, (double)0.0, (double)0.0);
            GL11.glVertex3d((double)0.375, (double)0.0, (double)0.0);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPopMatrix();
        }
    }

    private void startEnd(boolean revert) {
        if (revert) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GL11.glEnable((int)2848);
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            GL11.glHint((int)3154, (int)4354);
        } else {
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
            GL11.glDisable((int)2848);
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
        GlStateManager.depthMask((!revert ? 1 : 0) != 0);
    }

    private boolean doesntContain(EntityPlayer entityPlayer) {
        return !Skeleton.mc.world.playerEntities.contains((Object)entityPlayer);
    }
}

