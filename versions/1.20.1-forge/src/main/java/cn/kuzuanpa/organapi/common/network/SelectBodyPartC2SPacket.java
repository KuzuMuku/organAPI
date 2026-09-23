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

package cn.kuzuanpa.organapi.common.network;

import cn.kuzuanpa.organapi.common.capability.IOrganHolder;
import cn.kuzuanpa.organapi.common.menu.OrganOverviewMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SelectBodyPartC2SPacket(int bodyPartIndex) {
    public static void encode(SelectBodyPartC2SPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.bodyPartIndex);
    }

    public static SelectBodyPartC2SPacket decode(FriendlyByteBuf buf) {
        return new SelectBodyPartC2SPacket(buf.readInt());
    }

    public static void handle(SelectBodyPartC2SPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof OrganOverviewMenu organOverviewMenu) {
                IOrganHolder.resolve(organOverviewMenu.getTarget()).ifPresent(IOrganHolder::markDirty);
                organOverviewMenu.setSelectedBodyPartIndex(packet.bodyPartIndex());
            }
        });
        context.setPacketHandled(true);
    }
}
