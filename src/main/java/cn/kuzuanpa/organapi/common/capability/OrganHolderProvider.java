/*
 * This class was created by <kuzuanpa>. It is distributed as
 * part of the organAPI Mod. Get the Source Code in github:
 * https://github.com/KuzuMuku/organAPI
 *
 * organAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.

 * organAPI is Open Source and distributed under the
 * AGPLv3 License: https://www.gnu.org/licenses/agpl-3.0.txt
 *
 */

package cn.kuzuanpa.organapi.common.capability;

import cn.kuzuanpa.organapi.OrganApiMod;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OrganHolderProvider implements ICapabilitySerializable<CompoundTag> {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "organ_holder");
    private static final String PERSISTENT_DATA_KEY = OrganApiMod.MOD_ID + ".organ_holder";

    private final Entity owner;
    private final PlayerOrganHolder organHolder;
    private final LazyOptional<IOrganHolder> optional;

    public OrganHolderProvider(Entity owner) {
        this.owner = owner;
        this.organHolder = new PlayerOrganHolder(owner);
        this.organHolder.setDirtyListener(this::saveToOwner);
        this.optional = LazyOptional.of(() -> organHolder);
        loadFromOwner();
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull net.minecraftforge.common.capabilities.Capability<T> cap, @Nullable Direction side) {
        return cap == OrganCapabilities.ORGAN_HOLDER ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = organHolder.serializeNBT();
        saveToOwner(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        organHolder.deserializeNBT(nbt);
        saveToOwner(nbt);
    }

    private void loadFromOwner() {
        CompoundTag persistentData = owner.getPersistentData();
        if (persistentData.contains(PERSISTENT_DATA_KEY)) {
            organHolder.deserializeNBT(persistentData.getCompound(PERSISTENT_DATA_KEY));
        }
    }

    private void saveToOwner() {
        saveToOwner(organHolder.serializeNBT());
    }

    private void saveToOwner(CompoundTag tag) {
        owner.getPersistentData().put(PERSISTENT_DATA_KEY, tag.copy());
    }
}
