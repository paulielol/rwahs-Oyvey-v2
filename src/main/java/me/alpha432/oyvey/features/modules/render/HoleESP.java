package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.util.RenderUtil;
import java.awt.Color;
import me.alpha432.oyvey.util.ColorUtil;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.util.BlockUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import me.alpha432.oyvey.event.events.Render3DEvent;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.features.modules.Module;

public class HoleESP extends Module
{
    public Setting<Boolean> renderOwn;
    public Setting<Boolean> fov;
    public Setting<Boolean> rainbow;
    private final Setting<Integer> range;
    private final Setting<Integer> rangeY;
    public Setting<Boolean> box;
    public Setting<Boolean> gradientBox;
    public Setting<Boolean> invertGradientBox;
    public Setting<Boolean> outline;
    public Setting<Boolean> gradientOutline;
    public Setting<Boolean> invertGradientOutline;
    public Setting<Double> height;
    private Setting<Integer> red;
    private Setting<Integer> green;
    private Setting<Integer> blue;
    private Setting<Integer> alpha;
    private Setting<Integer> boxAlpha;
    private Setting<Float> lineWidth;
    public Setting<Boolean> safeColor;
    private Setting<Integer> safeRed;
    private Setting<Integer> safeGreen;
    private Setting<Integer> safeBlue;
    private Setting<Integer> safeAlpha;
    public Setting<Boolean> customOutline;
    private Setting<Integer> cRed;
    private Setting<Integer> cGreen;
    private Setting<Integer> cBlue;
    private Setting<Integer> cAlpha;
    private Setting<Integer> safecRed;
    private Setting<Integer> safecGreen;
    private Setting<Integer> safecBlue;
    private Setting<Integer> safecAlpha;
    private static HoleESP INSTANCE;
    private int currentAlpha;

    public HoleESP() {
        super("HoleESP", "Shows safe spots.", Module.Category.RENDER, false, false, false);
        this.renderOwn = (Setting<Boolean>)this.register(new Setting("RenderOwn", (Object)true));
        this.fov = (Setting<Boolean>)this.register(new Setting("InFov", (Object)true));
        this.rainbow = (Setting<Boolean>)this.register(new Setting("Rainbow", (Object)false));
        this.range = (Setting<Integer>)this.register(new Setting("RangeX", (Object)0, (Object)0, (Object)10));
        this.rangeY = (Setting<Integer>)this.register(new Setting("RangeY", (Object)0, (Object)0, (Object)10));
        this.box = (Setting<Boolean>)this.register(new Setting("Box", (Object)true));
        this.gradientBox = (Setting<Boolean>)this.register(new Setting("Gradient", (Object)false, v -> (boolean)this.box.getValue()));
        this.invertGradientBox = (Setting<Boolean>)this.register(new Setting("ReverseGradient", (Object)false, v -> (boolean)this.gradientBox.getValue()));
        this.outline = (Setting<Boolean>)this.register(new Setting("Outline", (Object)true));
        this.gradientOutline = (Setting<Boolean>)this.register(new Setting("GradientOutline", (Object)false, v -> (boolean)this.outline.getValue()));
        this.invertGradientOutline = (Setting<Boolean>)this.register(new Setting("ReverseOutline", (Object)false, v -> (boolean)this.gradientOutline.getValue()));
        this.height = (Setting<Double>)this.register(new Setting("Height", (Object)0.0, (Object)(-2.0), (Object)2.0));
        this.red = (Setting<Integer>)this.register(new Setting("Red", (Object)0, (Object)0, (Object)255));
        this.green = (Setting<Integer>)this.register(new Setting("Green", (Object)255, (Object)0, (Object)255));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", (Object)0, (Object)0, (Object)255));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", (Object)255, (Object)0, (Object)255));
        this.boxAlpha = (Setting<Integer>)this.register(new Setting("BoxAlpha", (Object)125, (Object)0, (Object)255, v -> (boolean)this.box.getValue()));
        this.lineWidth = (Setting<Float>)this.register(new Setting("LineWidth", (Object)1.0f, (Object)0.1f, (Object)5.0f, v -> (boolean)this.outline.getValue()));
        this.safeColor = (Setting<Boolean>)this.register(new Setting("BedrockColor", (Object)false));
        this.safeRed = (Setting<Integer>)this.register(new Setting("BedrockRed", (Object)0, (Object)0, (Object)255, v -> (boolean)this.safeColor.getValue()));
        this.safeGreen = (Setting<Integer>)this.register(new Setting("BedrockGreen", (Object)255, (Object)0, (Object)255, v -> (boolean)this.safeColor.getValue()));
        this.safeBlue = (Setting<Integer>)this.register(new Setting("BedrockBlue", (Object)0, (Object)0, (Object)255, v -> (boolean)this.safeColor.getValue()));
        this.safeAlpha = (Setting<Integer>)this.register(new Setting("BedrockAlpha", (Object)255, (Object)0, (Object)255, v -> (boolean)this.safeColor.getValue()));
        this.customOutline = (Setting<Boolean>)this.register(new Setting("CustomLine", (Object)false, v -> (boolean)this.outline.getValue()));
        this.cRed = (Setting<Integer>)this.register(new Setting("OL-Red", (Object)0, (Object)0, (Object)255, v -> (boolean)this.customOutline.getValue() && (boolean)this.outline.getValue()));
        this.cGreen = (Setting<Integer>)this.register(new Setting("OL-Green", (Object)0, (Object)0, (Object)255, v -> (boolean)this.customOutline.getValue() && (boolean)this.outline.getValue()));
        this.cBlue = (Setting<Integer>)this.register(new Setting("OL-Blue", (Object)255, (Object)0, (Object)255, v -> (boolean)this.customOutline.getValue() && (boolean)this.outline.getValue()));
        this.cAlpha = (Setting<Integer>)this.register(new Setting("OL-Alpha", (Object)255, (Object)0, (Object)255, v -> (boolean)this.customOutline.getValue() && (boolean)this.outline.getValue()));
        this.safecRed = (Setting<Integer>)this.register(new Setting("OL-SafeRed", (Object)0, (Object)0, (Object)255, v -> (boolean)this.customOutline.getValue() && (boolean)this.outline.getValue() && (boolean)this.safeColor.getValue()));
        this.safecGreen = (Setting<Integer>)this.register(new Setting("OL-SafeGreen", (Object)255, (Object)0, (Object)255, v -> (boolean)this.customOutline.getValue() && (boolean)this.outline.getValue() && (boolean)this.safeColor.getValue()));
        this.safecBlue = (Setting<Integer>)this.register(new Setting("OL-SafeBlue", (Object)0, (Object)0, (Object)255, v -> (boolean)this.customOutline.getValue() && (boolean)this.outline.getValue() && (boolean)this.safeColor.getValue()));
        this.safecAlpha = (Setting<Integer>)this.register(new Setting("OL-SafeAlpha", (Object)255, (Object)0, (Object)255, v -> (boolean)this.customOutline.getValue() && (boolean)this.outline.getValue() && (boolean)this.safeColor.getValue()));
        this.currentAlpha = 0;
        this.setInstance();
    }

    private void setInstance() {
        HoleESP.INSTANCE = this;
    }

    public static HoleESP getInstance() {
        if (HoleESP.INSTANCE == null) {
            HoleESP.INSTANCE = new HoleESP();
        }
        return HoleESP.INSTANCE;
    }

    public void onRender3D(final Render3DEvent event) {
        assert HoleESP.mc.renderViewEntity != null;
        final Vec3i playerPos = new Vec3i(HoleESP.mc.renderViewEntity.posX, HoleESP.mc.renderViewEntity.posY, HoleESP.mc.renderViewEntity.posZ);
        for (int x = playerPos.getX() - (int)this.range.getValue(); x < playerPos.getX() + (int)this.range.getValue(); ++x) {
            for (int z = playerPos.getZ() - (int)this.range.getValue(); z < playerPos.getZ() + (int)this.range.getValue(); ++z) {
                for (int y = playerPos.getY() + (int)this.rangeY.getValue(); y > playerPos.getY() - (int)this.rangeY.getValue(); --y) {
                    final BlockPos pos = new BlockPos(x, y, z);
                    if (HoleESP.mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && HoleESP.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && HoleESP.mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) && (!pos.equals((Object)new BlockPos(HoleESP.mc.player.posX, HoleESP.mc.player.posY, HoleESP.mc.player.posZ)) || (boolean)this.renderOwn.getValue())) {
                        if (BlockUtil.isPosInFov(pos) || !(boolean)this.fov.getValue()) {
                            if (HoleESP.mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK) {
                                RenderUtil.drawBoxESP(pos, ((boolean)this.rainbow.getValue()) ? ColorUtil.rainbow((int)ClickGui.getInstance().rainbowHue.getValue()) : new Color((int)this.safeRed.getValue(), (int)this.safeGreen.getValue(), (int)this.safeBlue.getValue(), (int)this.safeAlpha.getValue()), (boolean)this.customOutline.getValue(), new Color((int)this.safecRed.getValue(), (int)this.safecGreen.getValue(), (int)this.safecBlue.getValue(), (int)this.safecAlpha.getValue()), (float)this.lineWidth.getValue(), (boolean)this.outline.getValue(), (boolean)this.box.getValue(), (int)this.boxAlpha.getValue(), true, (double)this.height.getValue(), (boolean)this.gradientBox.getValue(), (boolean)this.gradientOutline.getValue(), (boolean)this.invertGradientBox.getValue(), (boolean)this.invertGradientOutline.getValue(), this.currentAlpha);
                            }
                            else if (BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.down()).getBlock()) && BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.east()).getBlock()) && BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.west()).getBlock()) && BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.south()).getBlock())) {
                                if (BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.north()).getBlock())) {
                                    RenderUtil.drawBoxESP(pos, ((boolean)this.rainbow.getValue()) ? ColorUtil.rainbow((int)ClickGui.getInstance().rainbowHue.getValue()) : new Color((int)this.red.getValue(), (int)this.green.getValue(), (int)this.blue.getValue(), (int)this.alpha.getValue()), (boolean)this.customOutline.getValue(), new Color((int)this.cRed.getValue(), (int)this.cGreen.getValue(), (int)this.cBlue.getValue(), (int)this.cAlpha.getValue()), (float)this.lineWidth.getValue(), (boolean)this.outline.getValue(), (boolean)this.box.getValue(), (int)this.boxAlpha.getValue(), true, (double)this.height.getValue(), (boolean)this.gradientBox.getValue(), (boolean)this.gradientOutline.getValue(), (boolean)this.invertGradientBox.getValue(), (boolean)this.invertGradientOutline.getValue(), this.currentAlpha);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    static {
        HoleESP.INSTANCE = new HoleESP();
    }
}

