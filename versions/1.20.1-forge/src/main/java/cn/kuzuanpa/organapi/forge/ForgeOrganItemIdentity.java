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

package cn.kuzuanpa.organapi.forge;

import cn.kuzuanpa.organapi.platform.OrganItemIdentity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Forge item-identity: stores the organ id as a string on the stack's
 * {@code CompoundTag} (1.20.1 has no DataComponents). Mirrors tacz's
 * {@code getGunId}/{@code setGunId} NBT accessor.
 */
public final class ForgeOrganItemIdentity implements OrganItemIdentity {
    private static final String ORGAN_ID_TAG = "OrganId";

    @Override
    @Nullable
    public ResourceLocation getOrganId(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(ORGAN_ID_TAG, Tag.TAG_STRING)) {
            return null;
        }
        return ResourceLocation.tryParse(tag.getString(ORGAN_ID_TAG));
    }

    @Override
    public void setOrganId(ItemStack stack, ResourceLocation organId) {
        stack.getOrCreateTag().putString(ORGAN_ID_TAG, organId.toString());
    }
}
