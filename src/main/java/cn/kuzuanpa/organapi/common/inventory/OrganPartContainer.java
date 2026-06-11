package cn.kuzuanpa.organapi.common.inventory;

import cn.kuzuanpa.organapi.common.capability.IOrganHolder;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class OrganPartContainer implements Container {
    private final Player player;
    private ResourceLocation bodyPartId;

    public OrganPartContainer(Player player, ResourceLocation bodyPartId) {
        this.player = player;
        this.bodyPartId = bodyPartId;
    }

    public void setBodyPartId(ResourceLocation bodyPartId) {
        this.bodyPartId = bodyPartId;
    }

    public ResourceLocation getBodyPartId() {
        return bodyPartId;
    }

    @Override
    public int getContainerSize() {
        return getHolder().map(holder -> holder.getCapacity(bodyPartId)).orElse(0);
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < getContainerSize(); i++) {
            if (!getItem(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return getHolder().map(holder -> holder.getOrgan(bodyPartId, slot)).orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = getItem(slot);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return removeItemNoUpdate(slot);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return getHolder().map(holder -> holder.removeOrgan(bodyPartId, slot)).orElse(ItemStack.EMPTY);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack stored = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        getHolder().ifPresent(holder -> holder.setOrgan(bodyPartId, slot, stored));
    }

    @Override
    public void setChanged() {
        getHolder().ifPresent(IOrganHolder::markDirty);
    }

    @Override
    public boolean stillValid(Player player) {
        return player == this.player && player.isAlive();
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < getContainerSize(); i++) {
            setItem(i, ItemStack.EMPTY);
        }
    }

    private Optional<IOrganHolder> getHolder() {
        return IOrganHolder.resolve(player);
    }
}
