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

package cn.kuzuanpa.organapi.api.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

public class OrganStateCommittedEvent extends Event {
    private final Player viewer;
    private final Entity target;
    private final String menuType;
    private final boolean dirty;

    public OrganStateCommittedEvent(Player viewer, Entity target, String menuType, boolean dirty) {
        this.viewer = viewer;
        this.target = target;
        this.menuType = menuType;
        this.dirty = dirty;
    }

    public Player getViewer() {
        return viewer;
    }

    public Entity getTarget() {
        return target;
    }

    public String getMenuType() {
        return menuType;
    }

    public boolean wasDirty() {
        return dirty;
    }
}
