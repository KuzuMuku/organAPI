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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public record ResolvedBodyPlan(
        ResourceLocation id,
        List<BodyPartDefinition> bodyParts,
        Map<ResourceLocation, BodyPartDefinition> bodyPartsById
) {
    public ResolvedBodyPlan {
        bodyParts = List.copyOf(bodyParts);
        bodyPartsById = Map.copyOf(bodyPartsById);
    }

    public Collection<BodyPartDefinition> getBodyParts() {
        return bodyParts;
    }

    public List<ResourceLocation> getOrderedBodyPartIds() {
        return bodyParts.stream().map(BodyPartDefinition::id).toList();
    }

    public Optional<BodyPartDefinition> getBodyPart(ResourceLocation id) {
        return Optional.ofNullable(bodyPartsById.get(id));
    }

    public ResourceLocation getDefaultBodyPartId(ResourceLocation fallback) {
        return bodyParts.isEmpty() ? fallback : bodyParts.get(0).id();
    }
}
