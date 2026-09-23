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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.item.ItemStack;

/**
 * Forge-side item ↔ opaque-bytes encoding used to fill the
 * {@code OrganStateData.SlotData#item()} byte arrays. Keeps
 * {@code CompoundTag}/{@code NbtIo} out of the core; step 4's NeoForge port will
 * supply its own equivalent (item DataComponents → bytes).
 */
final class ItemStackBytes {
    private ItemStackBytes() {
    }

    static byte[] toBytes(ItemStack stack) {
        CompoundTag tag = new CompoundTag();
        stack.save(tag);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            NbtIo.write(tag, new java.io.DataOutputStream(buffer));
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
        return buffer.toByteArray();
    }

    static ItemStack fromBytes(byte[] bytes) {
        try {
            CompoundTag tag = NbtIo.read(new java.io.DataInputStream(new ByteArrayInputStream(bytes)));
            return tag == null ? ItemStack.EMPTY : ItemStack.of(tag);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }
}
