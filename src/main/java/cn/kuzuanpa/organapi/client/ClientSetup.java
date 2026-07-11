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

package cn.kuzuanpa.organapi.client;

import cn.kuzuanpa.organapi.client.screen.BodyPartSelectionScreen;
import cn.kuzuanpa.organapi.client.screen.OrganOverviewScreen;
import cn.kuzuanpa.organapi.client.screen.OrganScreen;
import cn.kuzuanpa.organapi.common.registry.OrganMenus;
import net.minecraft.client.gui.screens.MenuScreens;

public final class ClientSetup {
    private ClientSetup() {
    }

    public static void registerScreens() {
        MenuScreens.register(OrganMenus.ORGAN_MENU.get(), OrganScreen::new);
        MenuScreens.register(OrganMenus.BODY_PART_SELECTION_MENU.get(), BodyPartSelectionScreen::new);
        MenuScreens.register(OrganMenus.ORGAN_OVERVIEW_MENU.get(), OrganOverviewScreen::new);
    }
}
