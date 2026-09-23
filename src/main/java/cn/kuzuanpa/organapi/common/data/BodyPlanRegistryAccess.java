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

import cn.kuzuanpa.organapi.core.model.BodyPlanDefinition;
import cn.kuzuanpa.organapi.core.model.OrganId;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory body-plan registry (platform class). Keyed by core {@link OrganId};
 * the entity-type lookup key is the core id derived from the loader's
 * {@code ResourceLocation} at the resolver boundary.
 */
public final class BodyPlanRegistryAccess {
    public static final OrganId DEFAULT_PLAN_ID = OrganId.of("organapi", "humanoid");

    private static Map<OrganId, BodyPlanDefinition> bodyPlans = Map.of();
    private static Map<OrganId, OrganId> entityTypePlans = Map.of();

    private BodyPlanRegistryAccess() {
    }

    public static Collection<BodyPlanDefinition> getBodyPlans() {
        return bodyPlans.values();
    }

    public static Optional<BodyPlanDefinition> getBodyPlan(OrganId id) {
        return Optional.ofNullable(bodyPlans.get(id));
    }

    public static Optional<BodyPlanDefinition> getBodyPlanForEntityType(OrganId entityTypeId) {
        OrganId planId = entityTypePlans.get(entityTypeId);
        if (planId == null) {
            return Optional.empty();
        }
        return getBodyPlan(planId);
    }

    public static void replaceBodyPlans(Map<OrganId, BodyPlanDefinition> replacements) {
        Map<OrganId, BodyPlanDefinition> orderedPlans = new LinkedHashMap<>(replacements);
        Map<OrganId, OrganId> mappings = new LinkedHashMap<>();
        for (Map.Entry<OrganId, BodyPlanDefinition> entry : orderedPlans.entrySet()) {
            for (OrganId entityTypeId : entry.getValue().entityTypes()) {
                mappings.put(entityTypeId, entry.getKey());
            }
        }
        bodyPlans = Map.copyOf(orderedPlans);
        entityTypePlans = Map.copyOf(mappings);
    }
}
