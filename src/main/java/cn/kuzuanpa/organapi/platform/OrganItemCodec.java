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

package cn.kuzuanpa.organapi.platform;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

/**
 * Item ↔ opaque-bytes encoding seam. Forge encodes with {@code ItemStack.save}/
 * {@code NbtIo}; the NeoForge 1.21.1 port encodes with the DataComponent-aware
 * {@code ItemStack.save(HolderLookup.Provider, …)} / {@code ItemStack.parse}. The
 * {@code Entity} context supplies the loader's {@code registryAccess()} where the
 * 1.21.1 API requires a {@code HolderLookup.Provider}.
 *
 * <p>The produced bytes are opaque and loader-local: they fill
 * {@link cn.kuzuanpa.organapi.core.state.OrganStateData.SlotData#item()} and never
 * cross between loaders (each loader persists/syncs with its own encoding), so the
 * DataComponents/NBT format divergence stays behind this seam and out of the core.
 */
public interface OrganItemCodec {
    /** Encodes a stack into loader-local opaque bytes, using {@code ctx} for registry access. */
    byte[] encode(Entity ctx, ItemStack stack);

    /** Decodes loader-local opaque bytes back into a stack, using {@code ctx} for registry access. */
    ItemStack decode(Entity ctx, byte[] bytes);
}
