
package me.alpha432.oyvey.mixin.mixins;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.modules.client.SkinChanger;
import me.alpha432.oyvey.features.modules.render.Chams;
import me.alpha432.oyvey.util.SkinStorageManipulationer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ RenderPlayer.class })

public class MixinRenderPlayer {
    @Inject(method = "renderEntityName", at = @At("HEAD"), cancellable = true)
    public void renderEntityNameHook(AbstractClientPlayer entityIn, double x, double y, double z, String name, double distanceSq, CallbackInfo info) {
        if (OyVey.moduleManager.isModuleEnabled("NameTags"))
            info.cancel();
    }

    @Overwrite
    public ResourceLocation getEntityTexture(final AbstractClientPlayer entity) {
        if (SkinChanger.instance.isEnabled() && entity == Minecraft.getMinecraft().player) {
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            return new ResourceLocation(SkinStorageManipulationer.getTexture().toString());
        }
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        return entity.getLocationSkin();
    }

}
