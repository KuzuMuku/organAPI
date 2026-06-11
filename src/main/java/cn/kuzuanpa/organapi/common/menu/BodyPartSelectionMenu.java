package cn.kuzuanpa.organapi.common.menu;

import cn.kuzuanpa.organapi.api.query.BodyPartOverview;
import cn.kuzuanpa.organapi.api.query.OrganQueryService;
import cn.kuzuanpa.organapi.common.data.OrganRegistryAccess;
import cn.kuzuanpa.organapi.common.registry.OrganMenus;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BodyPartSelectionMenu extends AbstractContainerMenu implements SelectableBodyPartMenu {
    private final Player player;
    private final List<ResourceLocation> bodyParts;
    private int selectedBodyPartIndex;

    public BodyPartSelectionMenu(int containerId, Inventory inventory) {
        super(OrganMenus.BODY_PART_SELECTION_MENU.get(), containerId);
        this.player = inventory.player;
        this.bodyParts = new ArrayList<>(OrganRegistryAccess.getOrderedBodyPartIds());
        if (this.bodyParts.isEmpty()) {
            this.bodyParts.add(ResourceLocation.fromNamespaceAndPath("organapi", "head"));
        }
        addPlayerInventory(inventory);
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return selectedBodyPartIndex;
            }

            @Override
            public void set(int value) {
                selectedBodyPartIndex = Math.max(0, Math.min(value, bodyParts.size() - 1));
            }
        });
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 142 + column * 18, 156 + row * 18));
            }
        }
        for (int hotbar = 0; hotbar < 9; hotbar++) {
            addSlot(new Slot(inventory, hotbar, 142 + hotbar * 18, 216));
        }
    }

    public Player getPlayer() {
        return player;
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
        broadcastChanges();
    }

    public ResourceLocation getSelectedBodyPartId() {
        return bodyParts.get(selectedBodyPartIndex);
    }

    public BodyPartOverview getOverview(ResourceLocation bodyPartId) {
        return OrganQueryService.getOverview(player, bodyPartId);
    }

    public ItemStack getPreviewStack(ResourceLocation bodyPartId) {
        return OrganQueryService.getInstalledOrgans(player, bodyPartId).stream()
                .filter(stack -> !stack.isEmpty())
                .findFirst()
                .map(ItemStack::copy)
                .orElse(ItemStack.EMPTY);
    }

    public int getUsedCapacity(ResourceLocation bodyPartId) {
        return OrganQueryService.getUsedCapacity(player, bodyPartId);
    }

    public int getTotalCapacity(ResourceLocation bodyPartId) {
        return OrganQueryService.getTotalCapacity(player, bodyPartId);
    }

    @Override
    public boolean stillValid(Player player) {
        return player == this.player && player.isAlive();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
