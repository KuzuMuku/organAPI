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

package cn.kuzuanpa.organapi.neoforge;

import cn.kuzuanpa.organapi.api.event.OrganStateCommittedEvent;
import cn.kuzuanpa.organapi.platform.OrganEvents;
import net.neoforged.neoforge.common.NeoForge;

/** NeoForge {@link OrganEvents}: posts to {@code NeoForge.EVENT_BUS}. */
public final class NeoForgeOrganEvents implements OrganEvents {
    @Override
    public void postStateCommitted(OrganStateCommittedEvent event) {
        NeoForge.EVENT_BUS.post(event);
    }
}
