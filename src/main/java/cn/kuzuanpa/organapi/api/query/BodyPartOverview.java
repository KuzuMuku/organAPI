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

package cn.kuzuanpa.organapi.api.query;

import java.util.List;
import net.minecraft.world.item.ItemStack;

public record BodyPartOverview(
        int totalCapacity,
        int usedCapacity,
        List<ItemStack> organs
) {
    public BodyPartOverview {
        organs = List.copyOf(organs);
    }
}
