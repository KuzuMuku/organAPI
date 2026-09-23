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

import cn.kuzuanpa.organapi.common.capability.ItemStackBytes;
import cn.kuzuanpa.organapi.platform.OrganItemCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

/**
 * Forge implementation of {@link OrganItemCodec}: delegates to the existing
 * {@link ItemStackBytes} ({@code ItemStack.save} + {@code NbtIo}). The 1.20.1
 * item format has no {@code HolderLookup.Provider}, so {@code ctx} is unused here.
 */
public final class ForgeOrganItemCodec implements OrganItemCodec {
    @Override
    public byte[] encode(Entity ctx, ItemStack stack) {
        return ItemStackBytes.toBytes(stack);
    }

    @Override
    public ItemStack decode(Entity ctx, byte[] bytes) {
        return ItemStackBytes.fromBytes(bytes);
    }
}
