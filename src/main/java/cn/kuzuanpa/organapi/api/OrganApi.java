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

package cn.kuzuanpa.organapi.api;

import cn.kuzuanpa.organapi.api.body.BodyPartDefinition;
import cn.kuzuanpa.organapi.api.body.ResolvedBodyPlan;
import cn.kuzuanpa.organapi.api.install.OrganInstallResult;
import cn.kuzuanpa.organapi.api.organ.OrganDefinition;
import cn.kuzuanpa.organapi.common.body.BodyPlanResolver;
import cn.kuzuanpa.organapi.common.capability.IOrganHolder;
import cn.kuzuanpa.organapi.common.data.OrganRegistryAccess;
import cn.kuzuanpa.organapi.common.menu.OrganMenu;
import cn.kuzuanpa.organapi.common.menu.OrganOverviewMenu;
import cn.kuzuanpa.organapi.common.network.OrganApiNetwork;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public final class OrganApi {
    private OrganApi() {
    }

    public static Collection<BodyPartDefinition> getBodyParts() {
        return OrganRegistryAccess.getBodyParts();
    }

    public static List<BodyPartDefinition> getBodyParts(Entity entity) {
        return BodyPlanResolver.getBodyParts(entity);
    }

    public static Collection<OrganDefinition> getOrgans() {
        return OrganRegistryAccess.getOrgans();
    }

    public static Optional<BodyPartDefinition> getBodyPart(ResourceLocation id) {
        return OrganRegistryAccess.getBodyPart(id);
    }

    public static Optional<BodyPartDefinition> getBodyPart(Entity entity, ResourceLocation id) {
        return BodyPlanResolver.getBodyPart(entity, id);
    }

    public static ResolvedBodyPlan getBodyPlan(Entity entity) {
        return BodyPlanResolver.resolve(entity);
    }

    public static Optional<OrganDefinition> getOrgan(ResourceLocation id) {
        return OrganRegistryAccess.getOrgan(id);
    }

    public static Optional<OrganDefinition> getOrgan(ItemStack stack) {
        return OrganRegistryAccess.getOrgan(stack);
    }

    public static OrganInstallResult install(Entity entity, ResourceLocation bodyPartId, ItemStack stack) {
        OrganInstallResult result = IOrganHolder.get(entity)
                .map(holder -> holder.install(bodyPartId, stack))
                .orElseGet(() -> OrganInstallResult.fail(Component.translatable("message.organapi.no_holder")));
        syncEntity(result, entity);
        return result;
    }

    public static ItemStack getOrgan(Entity entity, ResourceLocation bodyPartId, int slotIndex) {
        return IOrganHolder.get(entity)
                .map(holder -> holder.getOrgan(bodyPartId, slotIndex))
                .orElse(ItemStack.EMPTY);
    }

    public static OrganInstallResult setOrgan(Entity entity, ResourceLocation bodyPartId, int slotIndex, ItemStack stack) {
        OrganInstallResult result = IOrganHolder.get(entity)
                .map(holder -> holder.trySetOrgan(bodyPartId, slotIndex, stack))
                .orElseGet(() -> OrganInstallResult.fail(Component.translatable("message.organapi.no_holder")));
        syncEntity(result, entity);
        return result;
    }

    public static ItemStack removeOrgan(Entity entity, ResourceLocation bodyPartId, int slotIndex) {
        ItemStack removed = IOrganHolder.get(entity)
                .map(holder -> {
                    ItemStack organ = holder.getOrgan(bodyPartId, slotIndex).copy();
                    OrganInstallResult result = holder.trySetOrgan(bodyPartId, slotIndex, ItemStack.EMPTY);
                    return result.success() ? organ : ItemStack.EMPTY;
                })
                .orElse(ItemStack.EMPTY);
        if (!removed.isEmpty()) {
            syncEntity(OrganInstallResult.success(Component.empty()), entity);
        }
        return removed;
    }

    public static boolean addCapacity(Entity entity, ResourceLocation bodyPartId, int amount) {
        boolean success = IOrganHolder.get(entity).map(holder -> holder.addBonusCapacity(bodyPartId, amount)).orElse(false);
        if (success) {
            syncEntity(OrganInstallResult.success(Component.empty()), entity);
        }
        return success;
    }

    private static void syncEntity(OrganInstallResult result, Entity entity) {
        if (!result.success()) {
            return;
        }
        if (entity instanceof ServerPlayer serverPlayer) {
            OrganApiNetwork.sync(serverPlayer, serverPlayer);
            if (serverPlayer.containerMenu instanceof OrganMenu organMenu) {
                organMenu.broadcastChanges();
            }
            if (serverPlayer.containerMenu instanceof OrganOverviewMenu organOverviewMenu) {
                organOverviewMenu.broadcastChanges();
            }
        }
    }
}
