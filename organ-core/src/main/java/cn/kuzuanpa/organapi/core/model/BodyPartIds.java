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

public final class BodyPartIds {
    public static final OrganId HEAD = OrganId.of("organapi", "head");
    public static final OrganId CHEST = OrganId.of("organapi", "chest");
    public static final OrganId ABDOMEN = OrganId.of("organapi", "abdomen");
    public static final OrganId LEFT_ARM = OrganId.of("organapi", "left_arm");
    public static final OrganId RIGHT_ARM = OrganId.of("organapi", "right_arm");
    public static final OrganId LEFT_LEG = OrganId.of("organapi", "left_leg");
    public static final OrganId RIGHT_LEG = OrganId.of("organapi", "right_leg");

    private BodyPartIds() {
    }
}
