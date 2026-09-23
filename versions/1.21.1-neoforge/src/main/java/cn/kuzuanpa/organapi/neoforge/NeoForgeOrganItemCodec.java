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

import cn.kuzuanpa.organapi.platform.OrganItemCodec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

/**
 * NeoForge implementation of {@link OrganItemCodec}. 1.21.1 items carry
 * DataComponents rather than free-form NBT, so {@code ItemStack.save} now needs a
 * {@code HolderLookup.Provider}; we take it from the entity's
 * {@code level().registryAccess()}. The resulting {@link CompoundTag} is written
 * to a {@code byte[]} with {@code NbtIo}, mirroring the Forge encoding shape while
 * keeping the DataComponent divergence behind this seam.
 */
public final class NeoForgeOrganItemCodec implements OrganItemCodec {
    @Override
    public byte[] encode(Entity ctx, ItemStack stack) {
        if (stack.isEmpty()) {
            return new byte[0];
        }
        CompoundTag tag = (CompoundTag) stack.save(ctx.level().registryAccess(), new CompoundTag());
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            NbtIo.write(tag, new java.io.DataOutputStream(buffer));
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
        return buffer.toByteArray();
    }

    @Override
    public ItemStack decode(Entity ctx, byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return ItemStack.EMPTY;
        }
        try {
            CompoundTag tag = NbtIo.read(new java.io.DataInputStream(new ByteArrayInputStream(bytes)));
            return tag == null ? ItemStack.EMPTY : ItemStack.parseOptional(ctx.level().registryAccess(), tag);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }
}
