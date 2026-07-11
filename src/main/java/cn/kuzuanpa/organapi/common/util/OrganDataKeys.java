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

package cn.kuzuanpa.organapi.common.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class OrganDataKeys {
    /** Maximum organ slots current menus expose; gameplay capacity is defined by body-part data. */
    public static final int MAX_VISIBLE_SLOTS = 36;
    public static final ResourceLocation DEFAULT_BODY_PART = ResourceLocation.fromNamespaceAndPath("organapi", "head");
    public static final ItemStack EMPTY = ItemStack.EMPTY;

    private OrganDataKeys() {
    }
}
