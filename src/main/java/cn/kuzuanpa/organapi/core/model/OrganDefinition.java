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

import java.util.List;
import java.util.Set;

/**
 * Version-agnostic organ definition. Mirror of the former platform
 * {@code cn.kuzuanpa.organapi.api.organ.OrganDefinition} without any Minecraft
 * type; the platform adapts {@link OrganId} to/from {@code ResourceLocation}.
 */
public record OrganDefinition(
        OrganId id,
        OrganId itemId,
        Set<OrganId> validParts,
        int size,
        List<String> tooltips,
        List<String> tags
) {
    public OrganDefinition {
        validParts = Set.copyOf(validParts);
        tooltips = List.copyOf(tooltips);
        tags = List.copyOf(tags);
    }

    public boolean supports(OrganId partId) {
        return validParts.isEmpty() || validParts.contains(partId);
    }
}
