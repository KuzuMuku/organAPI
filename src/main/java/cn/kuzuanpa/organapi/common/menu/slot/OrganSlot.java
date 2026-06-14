package cn.kuzuanpa.organapi.common.menu.slot;

import cn.kuzuanpa.organapi.common.data.OrganRegistryAccess;
import cn.kuzuanpa.organapi.common.menu.OrganSlotContext;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class OrganSlot extends Slot {
    private final OrganSlotContext menu;
    private final int organIndex;

    public OrganSlot(OrganSlotContext menu, Container container, int slot, int x, int y) {
        super(container, slot, x, y);
        this.menu = menu;
        this.organIndex = slot;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return isActive()
                && OrganRegistryAccess.getBodyPart(menu.getSelectedBodyPartId()).map(def -> def.accepts(stack)).orElse(false)
                && OrganRegistryAccess.getOrgan(stack).map(def -> def.supports(menu.getSelectedBodyPartId())).orElse(true);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean mayPickup(@NotNull Player player) {
        return isActive();
    }

    @Override
    public boolean isActive() {
        return menu.isOrganSlotEnabled(organIndex);
    }
}
