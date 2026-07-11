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

package cn.kuzuanpa.organapi.common.inventory;

import cn.kuzuanpa.organapi.common.capability.IOrganHolder;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class OrganPartContainer implements Container {
    private final Player viewer;
    private final Entity target;
    private ResourceLocation bodyPartId;

    public OrganPartContainer(Player viewer, Entity target, ResourceLocation bodyPartId) {
        this.viewer = viewer;
        this.target = target;
        this.bodyPartId = bodyPartId;
    }

    public void setBodyPartId(ResourceLocation bodyPartId) {
        this.bodyPartId = bodyPartId;
    }

    public ResourceLocation getBodyPartId() {
        return bodyPartId;
    }

    @Override
    public int getContainerSize() {
        return getHolder().map(holder -> holder.getCapacity(bodyPartId)).orElse(0);
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < getContainerSize(); i++) {
            if (!getItem(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return getHolder().map(holder -> holder.getOrgan(bodyPartId, slot)).orElse(ItemStack.EMPTY);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack stack = getItem(slot);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return removeItemNoUpdate(slot);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        return getHolder().map(holder -> holder.removeOrgan(bodyPartId, slot)).orElse(ItemStack.EMPTY);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack stored = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        getHolder().ifPresent(holder -> holder.setOrgan(bodyPartId, slot, stored));
    }

    @Override
    public void setChanged() {
        getHolder().ifPresent(IOrganHolder::markDirty);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return player == viewer && player.isAlive() && target.isAlive();
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < getContainerSize(); i++) {
            setItem(i, ItemStack.EMPTY);
        }
    }

    private Optional<IOrganHolder> getHolder() {
        return IOrganHolder.resolve(target);
    }
}
