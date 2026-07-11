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

package cn.kuzuanpa.organapi.common.menu;

import cn.kuzuanpa.organapi.api.query.BodyPartOverview;
import cn.kuzuanpa.organapi.api.query.OrganQueryService;
import cn.kuzuanpa.organapi.common.body.BodyPlanResolver;
import cn.kuzuanpa.organapi.common.registry.OrganMenus;
import cn.kuzuanpa.organapi.common.util.OrganDataKeys;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BodyPartSelectionMenu extends AbstractContainerMenu implements SelectableBodyPartMenu {
    private final Player player;
    private final Entity target;
    private final List<ResourceLocation> bodyParts;
    private int selectedBodyPartIndex;

    public BodyPartSelectionMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, inventory.player.getId());
    }

    public BodyPartSelectionMenu(int containerId, Inventory inventory, int targetEntityId) {
        super(OrganMenus.BODY_PART_SELECTION_MENU.get(), containerId);
        this.player = inventory.player;
        this.target = resolveTargetEntity(inventory, targetEntityId);
        this.bodyParts = new ArrayList<>(BodyPlanResolver.getOrderedBodyPartIds(target));
        if (this.bodyParts.isEmpty()) {
            this.bodyParts.add(BodyPlanResolver.getDefaultBodyPartId(target, OrganDataKeys.DEFAULT_BODY_PART));
        }
        addPlayerInventory(inventory);
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return selectedBodyPartIndex;
            }

            @Override
            public void set(int value) {
                selectedBodyPartIndex = Math.max(0, Math.min(value, bodyParts.size() - 1));
            }
        });
    }

    private static Entity resolveTargetEntity(Inventory inventory, int targetEntityId) {
        Entity entity = inventory.player.level().getEntity(targetEntityId);
        return entity != null ? entity : inventory.player;
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 142 + column * 18, 156 + row * 18));
            }
        }
        for (int hotbar = 0; hotbar < 9; hotbar++) {
            addSlot(new Slot(inventory, hotbar, 142 + hotbar * 18, 216));
        }
    }

    public Player getPlayer() {
        return player;
    }

    public Entity getTarget() {
        return target;
    }

    public int getTargetEntityId() {
        return target.getId();
    }

    @Override
    public List<ResourceLocation> getBodyPartIds() {
        return List.copyOf(bodyParts);
    }

    @Override
    public int getSelectedBodyPartIndex() {
        return selectedBodyPartIndex;
    }

    @Override
    public void setSelectedBodyPartIndex(int index) {
        selectedBodyPartIndex = Math.max(0, Math.min(index, bodyParts.size() - 1));
        broadcastChanges();
    }

    public ResourceLocation getSelectedBodyPartId() {
        return bodyParts.get(selectedBodyPartIndex);
    }

    public BodyPartOverview getOverview(ResourceLocation bodyPartId) {
        return OrganQueryService.getOverview(target, bodyPartId);
    }

    public ItemStack getPreviewStack(ResourceLocation bodyPartId) {
        return OrganQueryService.getInstalledOrgans(target, bodyPartId).stream()
                .filter(stack -> !stack.isEmpty())
                .findFirst()
                .map(ItemStack::copy)
                .orElse(ItemStack.EMPTY);
    }

    public int getUsedCapacity(ResourceLocation bodyPartId) {
        return OrganQueryService.getUsedCapacity(target, bodyPartId);
    }

    public int getTotalCapacity(ResourceLocation bodyPartId) {
        return OrganQueryService.getTotalCapacity(target, bodyPartId);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return player == this.player && player.isAlive() && target.isAlive();
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }
}
