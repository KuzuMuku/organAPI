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

import cn.kuzuanpa.organapi.client.ClientSetup;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * NeoForge client hooks. Screen registration goes through
 * {@code RegisterMenuScreensEvent} (the public {@code MenuScreens.register} of
 * Forge is package-private here), feeding the loader-neutral
 * {@link ClientSetup#registerScreens} bindings.
 */
public final class OrganApiNeoForgeClient {
    private OrganApiNeoForgeClient() {
    }

    public static void onRegisterScreens(RegisterMenuScreensEvent event) {
        ClientSetup.registerScreens(event);
    }
}
