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

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

/**
 * Server→client organ-state sync seam. Forge implements it with a
 * {@code SimpleChannel}; step 4's NeoForge port will use the payload registrar.
 * The packet body is the loader-independent {@code OrganStateData} codec bytes.
 */
public interface OrganNetwork {
    /** Registers packets/handlers on the loader's networking system. */
    void register();

    /** Sends {@code target}'s current organ snapshot to {@code viewer}. */
    void sync(ServerPlayer viewer, Entity target);
}
