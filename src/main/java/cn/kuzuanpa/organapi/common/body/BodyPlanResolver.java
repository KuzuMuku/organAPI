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

package cn.kuzuanpa.organapi.common.body;

import cn.kuzuanpa.organapi.common.data.BodyPlanRegistryAccess;
import cn.kuzuanpa.organapi.common.data.OrganRegistryAccess;
import cn.kuzuanpa.organapi.core.model.BodyPartDefinition;
import cn.kuzuanpa.organapi.core.model.BodyPlanDefinition;
import cn.kuzuanpa.organapi.core.model.OrganId;
import cn.kuzuanpa.organapi.core.model.ResolvedBodyPlan;
import cn.kuzuanpa.organapi.core.parse.BodyPlanMerger;
import cn.kuzuanpa.organapi.platform.OrganIds;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
//? if forge {
import net.minecraftforge.registries.ForgeRegistries;
//?}

/**
 * Platform resolver: maps an entity to its body plan (needs the Forge entity-type
 * registry) and delegates the pure merge to {@link BodyPlanMerger} in the core.
 * Accepts/returns {@code ResourceLocation} at its public boundary and converts to
 * core {@link OrganId} internally.
 */
public final class BodyPlanResolver {
    private BodyPlanResolver() {
    }

    public static ResolvedBodyPlan resolve(Entity entity) {
        return resolve(entity.getType());
    }

    public static ResolvedBodyPlan resolve(EntityType<?> entityType) {
        //? if forge {
        ResourceLocation typeKey = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
        //?} else {
        /*ResourceLocation typeKey = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        *///?}
        Optional<BodyPlanDefinition> plan = typeKey == null
                ? Optional.empty()
                : BodyPlanRegistryAccess.getBodyPlanForEntityType(OrganIds.toOrganId(typeKey));
        return plan.map(BodyPlanResolver::resolve).orElseGet(BodyPlanResolver::fallbackPlan);
    }

    public static List<ResourceLocation> getOrderedBodyPartIds(Entity entity) {
        return resolve(entity).getOrderedBodyPartIds().stream().map(OrganIds::toResourceLocation).toList();
    }

    public static List<BodyPartDefinition> getBodyParts(Entity entity) {
        return List.copyOf(resolve(entity).bodyParts());
    }

    public static Optional<BodyPartDefinition> getBodyPart(Entity entity, ResourceLocation bodyPartId) {
        return resolve(entity).getBodyPart(OrganIds.toOrganId(bodyPartId));
    }

    public static ResourceLocation getDefaultBodyPartId(Entity entity, ResourceLocation fallback) {
        return OrganIds.toResourceLocation(resolve(entity).getDefaultBodyPartId(OrganIds.toOrganId(fallback)));
    }

    private static ResolvedBodyPlan resolve(BodyPlanDefinition plan) {
        return BodyPlanMerger.resolve(plan, OrganRegistryAccess::getBodyPart);
    }

    private static ResolvedBodyPlan fallbackPlan() {
        return BodyPlanRegistryAccess.getBodyPlan(BodyPlanRegistryAccess.DEFAULT_PLAN_ID)
                .map(BodyPlanResolver::resolve)
                .orElseGet(() -> {
                    List<BodyPartDefinition> bodyParts = OrganRegistryAccess.getBodyParts().stream().toList();
                    Map<OrganId, BodyPartDefinition> byId = new LinkedHashMap<>();
                    for (BodyPartDefinition bodyPart : bodyParts) {
                        byId.put(bodyPart.id(), bodyPart);
                    }
                    return new ResolvedBodyPlan(BodyPlanRegistryAccess.DEFAULT_PLAN_ID, bodyParts, byId);
                });
    }
}
