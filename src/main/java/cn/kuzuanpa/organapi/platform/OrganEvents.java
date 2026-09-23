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

import cn.kuzuanpa.organapi.api.event.OrganStateCommittedEvent;

/**
 * Event-bus seam. Forge posts to {@code MinecraftForge.EVENT_BUS}; the NeoForge
 * 1.21.1 port posts to {@code NeoForge.EVENT_BUS}. The event object itself
 * ({@link OrganStateCommittedEvent}) stays common so downstream add-ons subscribe
 * to one type; only the bus that carries it is loader-specific.
 */
public interface OrganEvents {
    /** Fires {@code event} on the loader's game event bus. */
    void postStateCommitted(OrganStateCommittedEvent event);
}
