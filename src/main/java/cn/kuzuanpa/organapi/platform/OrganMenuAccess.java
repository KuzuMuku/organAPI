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

import java.util.function.Consumer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

/**
 * Menu-open seam. Forge opens server-side menus with
 * {@code NetworkHooks.openScreen}; the NeoForge 1.21.1 port uses
 * {@code ServerPlayer#openMenu(MenuProvider, Consumer)} (the extra-data buffer is
 * a {@code RegistryFriendlyByteBuf}, a {@link FriendlyByteBuf} subtype, so the
 * common writer signature carries across unchanged).
 */
public interface OrganMenuAccess {
    /** Opens {@code provider} for {@code player}, writing menu bootstrap data via {@code extraData}. */
    void openMenu(ServerPlayer player, MenuProvider provider, Consumer<FriendlyByteBuf> extraData);
}
