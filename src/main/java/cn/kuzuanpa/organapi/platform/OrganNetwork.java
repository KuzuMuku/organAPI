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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

/**
 * Server→client organ-state sync seam plus the two client→server UI packets.
 * Forge implements it with a {@code SimpleChannel}; the NeoForge 1.21.1 port uses
 * the payload registrar ({@code CustomPacketPayload} + {@code StreamCodec}).
 * The sync packet body is the loader-independent {@code OrganStateData} codec
 * bytes; the C2S packets carry only primitives / a {@code ResourceLocation}.
 */
public interface OrganNetwork {
    /** Registers packets/handlers on the loader's networking system. */
    void register();

    /** Sends {@code target}'s current organ snapshot to {@code viewer}. */
    void sync(ServerPlayer viewer, Entity target);

    /**
     * Client→server: open the per-body-part organ menu for {@code targetEntityId}
     * on {@code bodyPartId}. Sent from the body-part selection screen.
     */
    void sendOpenOrganMenu(int targetEntityId, ResourceLocation bodyPartId);

    /**
     * Client→server: select the body part at {@code bodyPartIndex} in the current
     * overview menu. Sent from the overview screen.
     */
    void sendSelectBodyPart(int bodyPartIndex);
}
