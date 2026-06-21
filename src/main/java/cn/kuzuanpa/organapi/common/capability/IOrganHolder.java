package cn.kuzuanpa.organapi.common.capability;

import cn.kuzuanpa.organapi.api.install.OrganInstallResult;
import cn.kuzuanpa.organapi.api.extension.OrganBootstrapApi;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

public interface IOrganHolder {
    String BOOTSTRAP_INITIALIZED_TAG = "bootstrap_initialized";

    int getCapacity(@NotNull ResourceLocation bodyPartId);

    int getUsedCapacity(@NotNull ResourceLocation bodyPartId);

    int getFreeCapacity(@NotNull ResourceLocation bodyPartId);

    @NotNull List<ItemStack> getInstalledOrgans(@NotNull ResourceLocation bodyPartId);

    @NotNull OrganInstallResult install(@NotNull ResourceLocation bodyPartId, @NotNull ItemStack stack);

    @NotNull ItemStack removeOrgan(@NotNull ResourceLocation bodyPartId, int slotIndex);

    @NotNull ItemStack getOrgan(@NotNull ResourceLocation bodyPartId, int slotIndex);

    void setOrgan(@NotNull ResourceLocation bodyPartId, int slotIndex, @NotNull ItemStack stack);

    @NotNull OrganInstallResult trySetOrgan(@NotNull ResourceLocation bodyPartId, int slotIndex, @NotNull ItemStack stack);

    int getBonusCapacity(@NotNull ResourceLocation bodyPartId);

    boolean addBonusCapacity(@NotNull ResourceLocation bodyPartId, int amount);

    void copyFrom(@NotNull IOrganHolder other);

    void markDirty();

    boolean isDirty();

    void clearDirty();

    /**
     * Returns whether this entity's organ state has already been bootstrapped.
     * Bootstrap is the one-time initialization that assigns the entity's organs
     * when its organ holder is first accessed for actual gameplay use, such as
     * opening the organ UI. Once bootstrapped, the organs belong to the entity
     * for the rest of its lifetime and must not be regenerated just because the
     * holder was reloaded from NBT.
     */
    boolean isBootstrapInitialized();

    /**
     * Marks whether this entity's organ state has completed its one-time
     * bootstrap initialization. This flag is part of the holder's persisted
     * state so reloading from NBT does not cause bootstrap to run again.
     */
    void setBootstrapInitialized(boolean initialized);

    static @NotNull LazyOptional<IOrganHolder> get(@NotNull Entity entity) {
        LazyOptional<IOrganHolder> optional = entity.getCapability(OrganCapabilities.ORGAN_HOLDER);
        optional.ifPresent(holder -> OrganBootstrapApi.bootstrap(entity, holder));
        return optional;
    }

    static @NotNull Optional<IOrganHolder> resolve(@NotNull Entity entity) {
        return get(entity).resolve();
    }
}
