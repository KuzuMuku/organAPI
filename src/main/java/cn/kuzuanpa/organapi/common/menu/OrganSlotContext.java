package cn.kuzuanpa.organapi.common.menu;

import net.minecraft.resources.ResourceLocation;

public interface OrganSlotContext {
    ResourceLocation getSelectedBodyPartId();

    boolean isOrganSlotEnabled(int index);
}
