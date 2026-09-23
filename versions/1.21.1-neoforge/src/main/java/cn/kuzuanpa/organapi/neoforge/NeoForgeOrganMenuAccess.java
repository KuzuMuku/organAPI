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

import cn.kuzuanpa.organapi.platform.OrganMenuAccess;
import java.util.function.Consumer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

/**
 * NeoForge {@link OrganMenuAccess}: opens server menus via
 * {@code ServerPlayer#openMenu(MenuProvider, Consumer)}. The 1.21.1 extra-data
 * buffer is a {@link RegistryFriendlyByteBuf} (a {@link FriendlyByteBuf}), so the
 * common {@code Consumer<FriendlyByteBuf>} writer is passed straight through.
 */
public final class NeoForgeOrganMenuAccess implements OrganMenuAccess {
    @Override
    public void openMenu(ServerPlayer player, MenuProvider provider, Consumer<FriendlyByteBuf> extraData) {
        player.openMenu(provider, (Consumer<RegistryFriendlyByteBuf>) buf -> extraData.accept(buf));
    }
}
