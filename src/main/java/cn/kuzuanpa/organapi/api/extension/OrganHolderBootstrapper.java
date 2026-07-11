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

package cn.kuzuanpa.organapi.api.extension;

import cn.kuzuanpa.organapi.common.capability.IOrganHolder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface OrganHolderBootstrapper {
    void bootstrap(@NotNull Entity entity, @NotNull IOrganHolder holder);
}
