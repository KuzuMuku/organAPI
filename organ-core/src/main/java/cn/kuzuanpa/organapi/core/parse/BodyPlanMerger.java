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

package cn.kuzuanpa.organapi.core.parse;

import cn.kuzuanpa.organapi.core.model.BodyPartDefinition;
import cn.kuzuanpa.organapi.core.model.BodyPlanDefinition;
import cn.kuzuanpa.organapi.core.model.OrganId;
import cn.kuzuanpa.organapi.core.model.ResolvedBodyPlan;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Pure merge of a {@link BodyPlanDefinition} against per-body-part templates.
 * Extracted from the platform {@code BodyPlanResolver}; contains no Minecraft
 * dependency. The platform supplies the template lookup (which reads its
 * registry) and handles entity→plan mapping.
 */
public final class BodyPlanMerger {
    private static final List<OrganId> DEFAULT_ACCEPTED_TAGS = List.of(BodyPartDefinition.ORGAN_TAG);

    private BodyPlanMerger() {
    }

    public static ResolvedBodyPlan resolve(BodyPlanDefinition plan, Function<OrganId, Optional<BodyPartDefinition>> templateLookup) {
        Map<OrganId, BodyPartDefinition> resolvedParts = new LinkedHashMap<>();
        for (Map.Entry<OrganId, BodyPlanDefinition.PartDefinition> entry : plan.parts().entrySet()) {
            if (!entry.getValue().enabled()) {
                continue;
            }
            OrganId bodyPartId = entry.getKey();
            BodyPlanDefinition.PartDefinition override = entry.getValue();
            Optional<BodyPartDefinition> template = templateLookup.apply(bodyPartId);
            String translationKey = override.translationKey() != null
                    ? override.translationKey()
                    : template.map(BodyPartDefinition::translationKey).orElse("body_part." + bodyPartId.namespace() + "." + bodyPartId.path());
            int defaultCapacity = Math.max(0, override.capacity() != null
                    ? override.capacity()
                    : template.map(BodyPartDefinition::defaultCapacity).orElse(0));
            Integer maxCapacity = override.maxCapacity() != null
                    ? Integer.valueOf(Math.max(defaultCapacity, override.maxCapacity()))
                    : template.map(BodyPartDefinition::maxCapacity).orElse(null);
            int sortOrder = override.sortOrder() != null
                    ? override.sortOrder()
                    : template.map(BodyPartDefinition::sortOrder).orElse(resolvedParts.size());
            List<OrganId> acceptedTags = override.acceptedTags() != null
                    ? override.acceptedTags()
                    : template.map(BodyPartDefinition::acceptedTags).orElse(DEFAULT_ACCEPTED_TAGS);
            float visualWidthRatio = override.visualWidthRatio() != null
                    ? override.visualWidthRatio()
                    : template.map(BodyPartDefinition::visualWidthRatio).orElse(1.0F);
            float visualHeightRatio = override.visualHeightRatio() != null
                    ? override.visualHeightRatio()
                    : template.map(BodyPartDefinition::visualHeightRatio).orElse(1.0F);
            BodyPartDefinition.OverviewArea overviewArea = override.overviewArea() != null
                    ? override.overviewArea()
                    : template.map(BodyPartDefinition::overviewArea).orElse(null);
            resolvedParts.put(bodyPartId, new BodyPartDefinition(bodyPartId, translationKey, defaultCapacity, maxCapacity, sortOrder,
                    acceptedTags, visualWidthRatio, visualHeightRatio, overviewArea));
        }
        List<BodyPartDefinition> orderedParts = new ArrayList<>(resolvedParts.values());
        orderedParts.sort(Comparator.comparingInt(BodyPartDefinition::sortOrder));
        Map<OrganId, BodyPartDefinition> orderedMap = new LinkedHashMap<>();
        for (BodyPartDefinition definition : orderedParts) {
            orderedMap.put(definition.id(), definition);
        }
        return new ResolvedBodyPlan(plan.id(), orderedParts, orderedMap);
    }
}
