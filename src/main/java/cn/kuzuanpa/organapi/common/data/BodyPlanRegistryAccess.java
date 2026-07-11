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

package cn.kuzuanpa.organapi.common.data;

import cn.kuzuanpa.organapi.api.body.BodyPlanDefinition;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public final class BodyPlanRegistryAccess {
    public static final ResourceLocation DEFAULT_PLAN_ID = ResourceLocation.fromNamespaceAndPath("organapi", "humanoid");

    private static Map<ResourceLocation, BodyPlanDefinition> bodyPlans = Map.of();
    private static Map<ResourceLocation, ResourceLocation> entityTypePlans = Map.of();

    private BodyPlanRegistryAccess() {
    }

    public static Collection<BodyPlanDefinition> getBodyPlans() {
        return bodyPlans.values();
    }

    public static Optional<BodyPlanDefinition> getBodyPlan(ResourceLocation id) {
        return Optional.ofNullable(bodyPlans.get(id));
    }

    public static Optional<BodyPlanDefinition> getBodyPlanForEntityType(ResourceLocation entityTypeId) {
        ResourceLocation planId = entityTypePlans.get(entityTypeId);
        if (planId == null) {
            return Optional.empty();
        }
        return getBodyPlan(planId);
    }

    public static void replaceBodyPlans(Map<ResourceLocation, BodyPlanDefinition> replacements) {
        Map<ResourceLocation, BodyPlanDefinition> orderedPlans = new LinkedHashMap<>(replacements);
        Map<ResourceLocation, ResourceLocation> mappings = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, BodyPlanDefinition> entry : orderedPlans.entrySet()) {
            for (ResourceLocation entityTypeId : entry.getValue().entityTypes()) {
                mappings.put(entityTypeId, entry.getKey());
            }
        }
        bodyPlans = Map.copyOf(orderedPlans);
        entityTypePlans = Map.copyOf(mappings);
    }
}
