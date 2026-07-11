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

package cn.kuzuanpa.organapi.api.query;

import cn.kuzuanpa.organapi.common.body.BodyPlanResolver;
import cn.kuzuanpa.organapi.common.capability.IOrganHolder;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public final class OrganQueryService {
    private OrganQueryService() {
    }

    public static int getTotalCapacity(Entity entity, ResourceLocation bodyPartId) {
        return IOrganHolder.get(entity).map(holder -> holder.getCapacity(bodyPartId)).orElse(0);
    }

    public static int getUsedCapacity(Entity entity, ResourceLocation bodyPartId) {
        return IOrganHolder.get(entity).map(holder -> holder.getUsedCapacity(bodyPartId)).orElse(0);
    }

    public static int getFreeCapacity(Entity entity, ResourceLocation bodyPartId) {
        return IOrganHolder.get(entity).map(holder -> holder.getFreeCapacity(bodyPartId)).orElse(0);
    }

    public static List<ItemStack> getInstalledOrgans(Entity entity, ResourceLocation bodyPartId) {
        return IOrganHolder.get(entity).map(holder -> holder.getInstalledOrgans(bodyPartId)).orElse(Collections.emptyList());
    }

    public static ItemStack getOrgan(Entity entity, ResourceLocation bodyPartId, int slotIndex) {
        return IOrganHolder.get(entity).map(holder -> holder.getOrgan(bodyPartId, slotIndex)).orElse(ItemStack.EMPTY);
    }

    public static List<OrganPosition> getInstalledOrganPositions(Entity entity) {
        return BodyPlanResolver.getOrderedBodyPartIds(entity).stream()
                .flatMap(bodyPartId -> getInstalledOrganPositions(entity, bodyPartId).stream())
                .toList();
    }

    public static List<OrganPosition> getInstalledOrganPositions(Entity entity, ResourceLocation bodyPartId) {
        List<ItemStack> organs = getInstalledOrgans(entity, bodyPartId);
        return IntStream.range(0, organs.size())
                .mapToObj(index -> new OrganPosition(bodyPartId, index, organs.get(index)))
                .filter(position -> !position.organ().isEmpty())
                .toList();
    }

    public static BodyPartOverview getOverview(Entity entity, ResourceLocation bodyPartId) {
        return new BodyPartOverview(
                getTotalCapacity(entity, bodyPartId),
                getUsedCapacity(entity, bodyPartId),
                getInstalledOrgans(entity, bodyPartId)
        );
    }
}
