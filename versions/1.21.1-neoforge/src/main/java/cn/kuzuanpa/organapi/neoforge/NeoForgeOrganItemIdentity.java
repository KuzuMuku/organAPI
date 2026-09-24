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

package cn.kuzuanpa.organapi.neoforge;

import cn.kuzuanpa.organapi.OrganApiMod;
import cn.kuzuanpa.organapi.platform.OrganItemIdentity;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

/**
 * NeoForge 1.21.1 item-identity: stores the organ id in a registered
 * {@link DataComponentType} rather than free-form NBT (1.21 moved item data to
 * DataComponents). The component holds the {@code namespace:path} string; the seam
 * converts to/from {@link ResourceLocation}.
 */
public final class NeoForgeOrganItemIdentity implements OrganItemIdentity {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, OrganApiMod.MOD_ID);

    public static final Supplier<DataComponentType<String>> ORGAN_ID =
            COMPONENTS.register("organ_id", () -> DataComponentType.<String>builder()
                    .persistent(com.mojang.serialization.Codec.STRING)
                    .networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8)
                    .build());

    @Override
    @Nullable
    public ResourceLocation getOrganId(ItemStack stack) {
        String value = stack.get(ORGAN_ID.get());
        return value == null ? null : ResourceLocation.tryParse(value);
    }

    @Override
    public void setOrganId(ItemStack stack, ResourceLocation organId) {
        stack.set(ORGAN_ID.get(), organId.toString());
    }
}
