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

package cn.kuzuanpa.organapi.api.body;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public record BodyPlanDefinition(
        ResourceLocation id,
        List<ResourceLocation> entityTypes,
        Map<ResourceLocation, PartDefinition> parts
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
            List<TagKey<Item>> acceptedTags,
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
