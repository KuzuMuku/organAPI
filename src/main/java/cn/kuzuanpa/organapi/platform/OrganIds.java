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

package cn.kuzuanpa.organapi.platform;

import net.minecraft.resources.ResourceLocation;

/**
 * Bridges the version-agnostic {@link cn.kuzuanpa.organapi.core.model.OrganId}
 * used inside {@code organ-core} and Minecraft's {@code ResourceLocation}. This
 * is the single conversion seam between the pure-Java core and the loader; core
 * code never references {@code ResourceLocation}, and MC-facing code converts
 * here rather than parsing ids by hand.
 */
public final class OrganIds {
    private OrganIds() {
    }

    public static ResourceLocation toResourceLocation(cn.kuzuanpa.organapi.core.model.OrganId id) {
        return ResourceLocation.fromNamespaceAndPath(id.namespace(), id.path());
    }

    public static cn.kuzuanpa.organapi.core.model.OrganId toOrganId(ResourceLocation location) {
        return cn.kuzuanpa.organapi.core.model.OrganId.of(location.getNamespace(), location.getPath());
    }
}
