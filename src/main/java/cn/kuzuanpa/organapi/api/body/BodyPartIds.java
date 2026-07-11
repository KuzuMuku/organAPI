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

import net.minecraft.resources.ResourceLocation;

public final class BodyPartIds {
    public static final ResourceLocation HEAD = ResourceLocation.fromNamespaceAndPath("organapi", "head");
    public static final ResourceLocation CHEST = ResourceLocation.fromNamespaceAndPath("organapi", "chest");
    public static final ResourceLocation ABDOMEN = ResourceLocation.fromNamespaceAndPath("organapi", "abdomen");
    public static final ResourceLocation LEFT_ARM = ResourceLocation.fromNamespaceAndPath("organapi", "left_arm");
    public static final ResourceLocation RIGHT_ARM = ResourceLocation.fromNamespaceAndPath("organapi", "right_arm");
    public static final ResourceLocation LEFT_LEG = ResourceLocation.fromNamespaceAndPath("organapi", "left_leg");
    public static final ResourceLocation RIGHT_LEG = ResourceLocation.fromNamespaceAndPath("organapi", "right_leg");

    private BodyPartIds() {
    }
}
