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
//? if forge {
import net.minecraft.client.gui.screens.MenuScreens;
//?} else {
/*import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
*///?}

/**
 * Common screen bindings. The three menu→screen pairs are shared; only the
 * registration entry point differs per loader: Forge exposes a public
 * {@code MenuScreens.register}, while NeoForge 1.21.1 registers through
 * {@code RegisterMenuScreensEvent} (its {@code MenuScreens.register} is
 * package-private). Because the neoforge call site needs the generic bound
 * {@code U extends Screen & MenuAccess<M>} inferred at each concrete screen type,
 * the two bodies are kept as explicit per-loader calls via {@code //?} rather than
 * a shared functional interface (which cannot carry that intersection bound).
 */
public final class ClientSetup {
    private ClientSetup() {
    }

    //? if forge {
    /** Forge: register directly through the public {@code MenuScreens.register}. */
    public static void registerScreens() {
        MenuScreens.register(OrganMenus.ORGAN_MENU.get(), OrganScreen::new);
        MenuScreens.register(OrganMenus.BODY_PART_SELECTION_MENU.get(), BodyPartSelectionScreen::new);
        MenuScreens.register(OrganMenus.ORGAN_OVERVIEW_MENU.get(), OrganOverviewScreen::new);
    }
    //?} else {
    /*public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(OrganMenus.ORGAN_MENU.get(), OrganScreen::new);
        event.register(OrganMenus.BODY_PART_SELECTION_MENU.get(), BodyPartSelectionScreen::new);
        event.register(OrganMenus.ORGAN_OVERVIEW_MENU.get(), OrganOverviewScreen::new);
    }
    *///?}
}
