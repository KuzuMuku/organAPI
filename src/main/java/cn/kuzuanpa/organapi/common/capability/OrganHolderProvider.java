package cn.kuzuanpa.organapi.common.capability;

import cn.kuzuanpa.organapi.OrganApiMod;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OrganHolderProvider implements ICapabilitySerializable<CompoundTag> {
    public static final ResourceLocation ID = new ResourceLocation(OrganApiMod.MOD_ID, "organ_holder");

    private final PlayerOrganHolder organHolder = new PlayerOrganHolder();
    private final LazyOptional<IOrganHolder> optional = LazyOptional.of(() -> organHolder);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull net.minecraftforge.common.capabilities.Capability<T> cap, @Nullable Direction side) {
        return cap == OrganCapabilities.ORGAN_HOLDER ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return organHolder.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        organHolder.deserializeNBT(nbt);
    }
}
