package cn.kuzuanpa.organapi.common.capability;

import cn.kuzuanpa.organapi.api.install.OrganInstallResult;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

public interface IOrganHolder {
    int getCapacity(ResourceLocation bodyPartId);

    int getUsedCapacity(ResourceLocation bodyPartId);

    int getFreeCapacity(ResourceLocation bodyPartId);

    List<ItemStack> getInstalledOrgans(ResourceLocation bodyPartId);

    OrganInstallResult install(ResourceLocation bodyPartId, ItemStack stack);

    ItemStack removeOrgan(ResourceLocation bodyPartId, int slotIndex);

    ItemStack getOrgan(ResourceLocation bodyPartId, int slotIndex);

    void setOrgan(ResourceLocation bodyPartId, int slotIndex, ItemStack stack);

    OrganInstallResult trySetOrgan(ResourceLocation bodyPartId, int slotIndex, ItemStack stack);

    int getBonusCapacity(ResourceLocation bodyPartId);

    boolean addBonusCapacity(ResourceLocation bodyPartId, int amount);

    void copyFrom(IOrganHolder other);

    void markDirty();

    boolean isDirty();

    void clearDirty();

    static LazyOptional<IOrganHolder> get(Entity entity) {
        return entity.getCapability(OrganCapabilities.ORGAN_HOLDER);
    }

    static Optional<IOrganHolder> resolve(Entity entity) {
        return get(entity).resolve();
    }
}
