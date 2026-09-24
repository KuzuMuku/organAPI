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

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Item-identity seam for the single generic {@code organapi:organ_item}. The
 * concrete organ an {@link ItemStack} represents is carried as a stable
 * {@code namespace:path} identifier, stored per loader through this seam:
 * Forge writes a string on the item's {@code CompoundTag}, NeoForge 1.21.1 writes
 * a registered {@code DataComponent}. Only the item <em>identity</em> lives here —
 * organ holder <em>state</em> never touches item NBT (see {@link OrganItemCodec} /
 * {@link OrganStorage}). This mirrors tacz's {@code getGunId}/{@code setGunId}.
 */
public interface OrganItemIdentity {
    /** Reads the organ id stamped on {@code stack}, or {@code null} if absent. */
    @Nullable
    ResourceLocation getOrganId(ItemStack stack);

    /** Stamps {@code organId} onto {@code stack}. */
    void setOrganId(ItemStack stack, ResourceLocation organId);
}
