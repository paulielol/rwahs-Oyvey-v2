package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.features.modules.render.SkyColor;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.util.Timer;
import me.alpha432.oyvey.util.InventoryUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.text.TextComponentString;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


public class ArmourMend extends Module {
    private final Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 50, 0, 500));
    private final Setting<Boolean> mendingTakeOff = this.register(new Setting<Boolean>("Auto Mend", false));
    private final Setting<Integer> enemyRange = this.register(new Setting<Object>("Enemy Range", Integer.valueOf(8), Integer.valueOf(0), Integer.valueOf(25), v -> this.mendingTakeOff.getValue()));
    private final Setting<Integer> helmetThreshold = this.register(new Setting<Object>("Helmet", Integer.valueOf(80), Integer.valueOf(0), Integer.valueOf(100), v -> this.mendingTakeOff.getValue()));
    private final Setting<Integer> chestThreshold = this.register(new Setting<Object>("Chestplate", Integer.valueOf(80), Integer.valueOf(0), Integer.valueOf(100), v -> this.mendingTakeOff.getValue()));
    private final Setting<Integer> legThreshold = this.register(new Setting<Object>("Leggings", Integer.valueOf(80), Integer.valueOf(0), Integer.valueOf(100), v -> this.mendingTakeOff.getValue()));
    private final Setting<Integer> bootsThreshold = this.register(new Setting<Object>("Boots", Integer.valueOf(80), Integer.valueOf(0), Integer.valueOf(100), v -> this.mendingTakeOff.getValue()));
    private final Setting<Integer> actions = this.register(new Setting<Object>("Actions", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(10), v -> this.mendingTakeOff.getValue()));
    private final Timer timer = new Timer();
    private final Queue<InventoryUtil.Task> taskList = new ConcurrentLinkedQueue<>();
    private final List<Integer> doneSlots = new ArrayList<>();
    private static ArmourMend INSTANCE = new ArmourMend();
    boolean flag;

    public ArmourMend() {
        super("Armour Mend", "switches armor", Category.MISC, true, false, false);
        this.setInstance();
    }

    public static ArmourMend getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ArmourMend();
        }
        return INSTANCE;
    }

    public void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onLogin() {
        this.timer.reset();
    }

    @Override
    public void onDisable() {
        this.taskList.clear();
        this.doneSlots.clear();
        this.flag = false;
    }

    @Override
    public void onLogout() {
        this.taskList.clear();
        this.doneSlots.clear();
    }


    @Override
    public void onTick() {
        if (nullCheck() || mc.currentScreen instanceof GuiContainer && !(mc.currentScreen instanceof GuiInventory)) {
            return;
        }
        if (this.taskList.isEmpty()) {
            int slot;
            int slot2;
            int slot3;
            int slot4;
            if (this.mendingTakeOff.getValue() && InventoryUtil.holdingItem(ItemExpBottle.class) &&
                    mc.gameSettings.keyBindUseItem.isKeyDown() &&
                    mc.world.playerEntities.stream().noneMatch(e ->
                            e != mc.player && !OyVey.friendManager.isFriend(e.getName())
                                    && mc.player.getDistance(e) <= (float) this.enemyRange.getValue()
                    ) && !this.flag) {
                int dam;
                int takeOff = 0;
                for (Map.Entry<Integer, ItemStack> armorSlot : this.getArmor().entrySet()) {
                    ItemStack stack = armorSlot.getValue();
                    float percent = (float) this.helmetThreshold.getValue() / 100.0f;
                    dam = Math.round((float) stack.getMaxDamage() * percent);
                    if (dam >= stack.getMaxDamage() - stack.getItemDamage()) continue;
                    ++takeOff;
                }
                if (takeOff == 4) {
                    this.flag = true;
                }
                if (!this.flag) {
                    ItemStack itemStack1 = mc.player.inventoryContainer.getSlot(5).getStack();
                    if (!itemStack1.isEmpty) {
                        float percent = (float) this.helmetThreshold.getValue() / 100.0f;
                        int dam2 = Math.round((float) itemStack1.getMaxDamage() * percent);
                        if (dam2 < itemStack1.getMaxDamage() - itemStack1.getItemDamage()) {
                            this.takeOffSlot(5);
                        }
                    }
                    ItemStack itemStack2 = mc.player.inventoryContainer.getSlot(6).getStack();
                    if (!itemStack2.isEmpty) {
                        float percent = (float) this.chestThreshold.getValue() / 100.0f;
                        int dam3 = Math.round((float) itemStack2.getMaxDamage() * percent);
                        if (dam3 < itemStack2.getMaxDamage() - itemStack2.getItemDamage()) {
                            this.takeOffSlot(6);
                        }
                    }
                    ItemStack itemStack3 = mc.player.inventoryContainer.getSlot(7).getStack();
                    if (!itemStack3.isEmpty) {
                        float percent = (float) this.legThreshold.getValue() / 100.0f;
                        dam = Math.round((float) itemStack3.getMaxDamage() * percent);
                        if (dam < itemStack3.getMaxDamage() - itemStack3.getItemDamage()) {
                            this.takeOffSlot(7);
                        }
                    }
                    ItemStack itemStack4 = mc.player.inventoryContainer.getSlot(8).getStack();
                    if (!itemStack4.isEmpty) {
                        float percent = (float) this.bootsThreshold.getValue() / 100.0f;
                        int dam4 = Math.round((float) itemStack4.getMaxDamage() * percent);
                        if (dam4 < itemStack4.getMaxDamage() - itemStack4.getItemDamage()) {
                            this.takeOffSlot(8);
                        }
                    }
                }
                return;
            }
            this.flag = false;
            ItemStack helm = mc.player.inventoryContainer.getSlot(5).getStack();
            if (helm.getItem() == Items.AIR && (slot4 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.HEAD, true, true)) != -1) {
                this.getSlotOn(5, slot4);
            }
            if (mc.player.inventoryContainer.getSlot(6).getStack().getItem() == Items.AIR && (slot3 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.CHEST, true, true)) != -1) {
                this.getSlotOn(6, slot3);
            }
            if (mc.player.inventoryContainer.getSlot(7).getStack().getItem() == Items.AIR && (slot2 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.LEGS, true, true)) != -1) {
                this.getSlotOn(7, slot2);
            }
            if (mc.player.inventoryContainer.getSlot(8).getStack().getItem() == Items.AIR && (slot = InventoryUtil.findArmorSlot(EntityEquipmentSlot.FEET, true, true)) != -1) {
                this.getSlotOn(8, slot);
            }
        }
        if (this.timer.passedMs((int) ((float) this.delay.getValue() * OyVey.serverManager.getTpsFactor()))) {
            if (!this.taskList.isEmpty()) {
                for (int i = 0; i < this.actions.getValue(); ++i) {
                    InventoryUtil.Task task = this.taskList.poll();
                    if (task == null) continue;
                    task.run();
                }
            }
            this.timer.reset();
        }
    }

    private void takeOffSlot(int slot) {
        if (this.taskList.isEmpty()) {
            int target = -1;
            for (int i : InventoryUtil.findEmptySlots(true)) {
                if (this.doneSlots.contains(target)) continue;
                target = i;
                this.doneSlots.add(i);
            }
            if (target != -1) {
                this.taskList.add(new InventoryUtil.Task(slot));
                this.taskList.add(new InventoryUtil.Task(target));
                this.taskList.add(new InventoryUtil.Task());
            }
        }
    }

    private void getSlotOn(int slot, int target) {
        if (this.taskList.isEmpty()) {
            this.doneSlots.remove((Object) target);
            this.taskList.add(new InventoryUtil.Task(target));
            this.taskList.add(new InventoryUtil.Task(slot));
            this.taskList.add(new InventoryUtil.Task());
        }
    }

    private Map<Integer, ItemStack> getArmor() {
        return this.getInventorySlots(5, 8);
    }

    private Map<Integer, ItemStack> getInventorySlots(int current, int last) {
        HashMap<Integer, ItemStack> fullInventorySlots = new HashMap<>();
        while (current <= last) {
            fullInventorySlots.put(current, mc.player.inventoryContainer.getInventory().get(current));
            ++current;
        }
        return fullInventorySlots;
    }

}

