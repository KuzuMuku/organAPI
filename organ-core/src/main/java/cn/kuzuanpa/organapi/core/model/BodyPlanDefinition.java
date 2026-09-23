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

package cn.kuzuanpa.organapi.core.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Version-agnostic body-plan definition. Mirror of the former platform
 * {@code cn.kuzuanpa.organapi.api.body.BodyPlanDefinition} with
 * {@code ResourceLocation}/{@code TagKey<Item>} replaced by {@link OrganId}.
 */
public record BodyPlanDefinition(
        OrganId id,
        List<OrganId> entityTypes,
        Map<OrganId, PartDefinition> parts
) {
    public BodyPlanDefinition {
        entityTypes = List.copyOf(entityTypes);
        parts = Map.copyOf(new LinkedHashMap<>(parts));
    }

    public record PartDefinition(
            boolean enabled,
            String translationKey,
            Integer capacity,
            Integer maxCapacity,
            Integer sortOrder,
            List<OrganId> acceptedTags,
            Float visualWidthRatio,
            Float visualHeightRatio,
            BodyPartDefinition.OverviewArea overviewArea
    ) {
        public PartDefinition {
            acceptedTags = acceptedTags == null ? null : List.copyOf(acceptedTags);
        }

        public static PartDefinition createEnabled() {
            return new PartDefinition(true, null, null, null, null, null, null, null, null);
        }
    }
}
