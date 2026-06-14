package cn.kuzuanpa.organapi.common.menu;

import cn.kuzuanpa.organapi.api.body.BodyPartDefinition;
import cn.kuzuanpa.organapi.common.body.BodyPlanResolver;
import cn.kuzuanpa.organapi.common.capability.IOrganHolder;
import cn.kuzuanpa.organapi.common.data.OrganRegistryAccess;
import cn.kuzuanpa.organapi.common.inventory.OrganPartContainer;
import cn.kuzuanpa.organapi.common.menu.slot.OrganSlot;
import cn.kuzuanpa.organapi.common.network.OrganApiNetwork;
import cn.kuzuanpa.organapi.common.registry.OrganMenus;
import cn.kuzuanpa.organapi.common.util.OrganDataKeys;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class OrganMenu extends AbstractContainerMenu implements OrganSlotContext {
    public static final int MAX_ORGAN_SLOTS = OrganDataKeys.MAX_VISIBLE_SLOTS;

    private final Player player;
    private final Entity target;
    private final OrganPartContainer organContainer;
    private final List<ResourceLocation> bodyParts;
    private int selectedBodyPartIndex;
    private int visibleOrganSlotCount;

    public OrganMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, inventory.player.getId(), OrganDataKeys.DEFAULT_BODY_PART);
    }

    public OrganMenu(int containerId, Inventory inventory, ResourceLocation initialBodyPart) {
        this(containerId, inventory, inventory.player.getId(), initialBodyPart);
    }

    public OrganMenu(int containerId, Inventory inventory, int targetEntityId, ResourceLocation initialBodyPart) {
        super(OrganMenus.ORGAN_MENU.get(), containerId);
        this.player = inventory.player;
        this.target = resolveTargetEntity(inventory, targetEntityId);
        this.bodyParts = new ArrayList<>(BodyPlanResolver.getOrderedBodyPartIds(target));
        if (this.bodyParts.isEmpty()) {
            this.bodyParts.add(BodyPlanResolver.getDefaultBodyPartId(target, OrganDataKeys.DEFAULT_BODY_PART));
        }
        this.selectedBodyPartIndex = resolveBodyPartIndex(initialBodyPart);
        this.organContainer = new OrganPartContainer(player, target, getSelectedBodyPartId());
        refreshMenuState();
        addOrganSlots();
        addPlayerInventory(inventory);
        addDataSlots();
    }

    private static Entity resolveTargetEntity(Inventory inventory, int targetEntityId) {
        Entity entity = inventory.player.level().getEntity(targetEntityId);
        return entity != null ? entity : inventory.player;
    }

    private int resolveBodyPartIndex(ResourceLocation bodyPartId) {
        int index = bodyParts.indexOf(bodyPartId);
        return index >= 0 ? index : 0;
    }

    private void addOrganSlots() {
        for (int index = 0; index < MAX_ORGAN_SLOTS; index++) {
            addSlot(new OrganSlot(this, organContainer, index, 0, 0));
        }
    }

    private void addPlayerInventory(Inventory inventory) {
        int startY = getPlayerInventoryStartY();
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, column * 18, startY + row * 18));
            }
        }
        for (int hotbar = 0; hotbar < 9; hotbar++) {
            addSlot(new Slot(inventory, hotbar,  hotbar * 18, startY + 58));
        }
    }

    private void addDataSlots() {
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return selectedBodyPartIndex;
            }

            @Override
            public void set(int value) {
                selectedBodyPartIndex = Math.max(0, Math.min(value, bodyParts.size() - 1));
                organContainer.setBodyPartId(getSelectedBodyPartId());
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return visibleOrganSlotCount;
            }

            @Override
            public void set(int value) {
                visibleOrganSlotCount = Math.max(0, Math.min(value, MAX_ORGAN_SLOTS));
            }
        });
    }

    private void refreshMenuState() {
        organContainer.setBodyPartId(getSelectedBodyPartId());
        visibleOrganSlotCount = Math.min(MAX_ORGAN_SLOTS, organContainer.getContainerSize());
    }

    public Player getPlayer() {
        return player;
    }

    public Entity getTarget() {
        return target;
    }

    public int getTargetEntityId() {
        return target.getId();
    }

    public List<BodyPartDefinition> getBodyPartDefinitions() {
        return BodyPlanResolver.getBodyParts(target);
    }

    public List<ResourceLocation> getBodyPartIds() {
        return List.copyOf(bodyParts);
    }

    @Override
    public ResourceLocation getSelectedBodyPartId() {
        return bodyParts.get(selectedBodyPartIndex);
    }

    public int getSelectedBodyPartIndex() {
        return selectedBodyPartIndex;
    }

    public int getVisibleOrganSlotCount() {
        return visibleOrganSlotCount;
    }

    public int getPlayerInventoryStartY() {
        return 154;
    }

    @Override
    public boolean isOrganSlotEnabled(int index) {
        return index >= 0 && index < visibleOrganSlotCount;
    }

    public int getUsedCapacity() {
        int used = 0;
        for (int i = 0; i < getVisibleOrganSlotCount(); i++) {
            ItemStack stack = slots.get(i).getItem();
            if (!stack.isEmpty()) {
                used += OrganRegistryAccess.getOrgan(stack).map(def -> def.size()).orElse(1);
            }
        }
        return used;
    }

    public void cycleBodyPart(int direction) {
        if (bodyParts.isEmpty()) {
            return;
        }
        int size = bodyParts.size();
        selectedBodyPartIndex = Math.floorMod(selectedBodyPartIndex + direction, size);
        refreshMenuState();
        broadcastFullState();
        broadcastChanges();
    }

    @Override
    public void broadcastChanges() {
        refreshMenuState();
        super.broadcastChanges();
        syncOrganDataIfDirty();
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        boolean wasDirty = player instanceof ServerPlayer
                && IOrganHolder.resolve(target).map(IOrganHolder::isDirty).orElse(false);
        syncOrganDataIfDirty();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(
                new cn.kuzuanpa.organapi.api.event.OrganStateCommittedEvent(player, target, "organ_menu", wasDirty));
    }

    private void syncOrganDataIfDirty() {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        IOrganHolder.resolve(target).ifPresent(holder -> {
            if (holder.isDirty()) {
                OrganApiNetwork.sync(serverPlayer, target);
                holder.clearDirty();
            }
        });
    }

    @Override
    public boolean stillValid(Player player) {
        return organContainer.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        int organSlotCount = MAX_ORGAN_SLOTS;
        if (index < organSlotCount) {
            if (!moveItemStackTo(stack, organSlotCount, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            boolean moved = false;
            for (int organIndex = 0; organIndex < getVisibleOrganSlotCount(); organIndex++) {
                Slot targetSlot = slots.get(organIndex);
                if (!targetSlot.hasItem() && targetSlot.mayPlace(stack)) {
                    ItemStack single = stack.copyWithCount(1);
                    targetSlot.set(single);
                    targetSlot.setChanged();
                    stack.shrink(1);
                    moved = true;
                    break;
                }
            }
            if (!moved) {
                return ItemStack.EMPTY;
            }
        }
        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        broadcastChanges();
        return copy;
    }
}
