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

package cn.kuzuanpa.organapi.api.organ;

import java.util.List;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public record OrganDefinition(
        ResourceLocation id,
        ResourceLocation itemId,
        Set<ResourceLocation> validParts,
        int size,
        List<String> tooltips,
        List<String> tags
) {
    public OrganDefinition {
        validParts = Set.copyOf(validParts);
        tooltips = List.copyOf(tooltips);
        tags = List.copyOf(tags);
    }

    public boolean supports(ResourceLocation partId) {
        return validParts.isEmpty() || validParts.contains(partId);
    }
}
