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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Version-agnostic resolved body plan. Mirror of the former platform
 * {@code cn.kuzuanpa.organapi.api.body.ResolvedBodyPlan}, keyed by {@link OrganId}.
 */
public record ResolvedBodyPlan(
        OrganId id,
        List<BodyPartDefinition> bodyParts,
        Map<OrganId, BodyPartDefinition> bodyPartsById
) {
    public ResolvedBodyPlan {
        bodyParts = List.copyOf(bodyParts);
        bodyPartsById = Map.copyOf(bodyPartsById);
    }

    public Collection<BodyPartDefinition> getBodyParts() {
        return bodyParts;
    }

    public List<OrganId> getOrderedBodyPartIds() {
        return bodyParts.stream().map(BodyPartDefinition::id).toList();
    }

    public Optional<BodyPartDefinition> getBodyPart(OrganId id) {
        return Optional.ofNullable(bodyPartsById.get(id));
    }

    public OrganId getDefaultBodyPartId(OrganId fallback) {
        return bodyParts.isEmpty() ? fallback : bodyParts.get(0).id();
    }
}
