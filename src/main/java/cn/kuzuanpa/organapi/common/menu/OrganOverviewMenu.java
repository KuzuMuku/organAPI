package cn.kuzuanpa.organapi.common.menu;

import cn.kuzuanpa.organapi.api.query.BodyPartOverview;
import cn.kuzuanpa.organapi.api.query.OrganQueryService;
import cn.kuzuanpa.organapi.common.body.BodyPlanResolver;
import cn.kuzuanpa.organapi.common.capability.IOrganHolder;
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

public class OrganOverviewMenu extends AbstractContainerMenu implements OrganSlotContext, SelectableBodyPartMenu {
    public static final int MAX_ORGAN_SLOTS = OrganDataKeys.MAX_VISIBLE_SLOTS;

    private final Player player;
    private final Entity target;
    private final OrganPartContainer organContainer;
    private final List<ResourceLocation> bodyParts;
    private int selectedBodyPartIndex;
    private int visibleOrganSlotCount;

    public OrganOverviewMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, inventory.player.getId(), OrganDataKeys.DEFAULT_BODY_PART);
    }

    public OrganOverviewMenu(int containerId, Inventory inventory, ResourceLocation initialBodyPart) {
        this(containerId, inventory, inventory.player.getId(), initialBodyPart);
    }

    public OrganOverviewMenu(int containerId, Inventory inventory, int targetEntityId, ResourceLocation initialBodyPart) {
        super(OrganMenus.ORGAN_OVERVIEW_MENU.get(), containerId);
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
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 0, 0));
            }
        }
        for (int hotbar = 0; hotbar < 9; hotbar++) {
            addSlot(new Slot(inventory, hotbar, 0, 0));
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

    @Override
    public List<ResourceLocation> getBodyPartIds() {
        return List.copyOf(bodyParts);
    }

    @Override
    public int getSelectedBodyPartIndex() {
        return selectedBodyPartIndex;
    }

    @Override
    public void setSelectedBodyPartIndex(int index) {
        selectedBodyPartIndex = Math.max(0, Math.min(index, bodyParts.size() - 1));
        refreshMenuState();
        broadcastFullState();
        broadcastChanges();
    }

    @Override
    public ResourceLocation getSelectedBodyPartId() {
        return bodyParts.get(selectedBodyPartIndex);
    }

    @Override
    public boolean isOrganSlotEnabled(int index) {
        return index >= 0 && index < visibleOrganSlotCount;
    }

    public int getVisibleOrganSlotCount() {
        return visibleOrganSlotCount;
    }

    public int getUsedCapacity(ResourceLocation bodyPartId) {
        return OrganQueryService.getUsedCapacity(target, bodyPartId);
    }

    public BodyPartOverview getOverview(ResourceLocation bodyPartId) {
        return OrganQueryService.getOverview(target, bodyPartId);
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
        syncOrganDataIfDirty();
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
