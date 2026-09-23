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

import cn.kuzuanpa.organapi.common.menu.OrganMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public record OpenOrganMenuC2SPacket(int targetEntityId, ResourceLocation bodyPartId) {
    public static void encode(OpenOrganMenuC2SPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.targetEntityId);
        buf.writeResourceLocation(packet.bodyPartId);
    }

    public static OpenOrganMenuC2SPacket decode(FriendlyByteBuf buf) {
        return new OpenOrganMenuC2SPacket(buf.readInt(), buf.readResourceLocation());
    }

    public static void handle(OpenOrganMenuC2SPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                Entity target = player.level().getEntity(packet.targetEntityId());
                if (target == null) {
                    return;
                }
                NetworkHooks.openScreen(player, new SimpleMenuProvider(
                        (windowId, inventory, entityPlayer) -> new OrganMenu(windowId, inventory, packet.targetEntityId(), packet.bodyPartId()),
                        Component.translatable("menu.organapi.organ_menu")
                ), buf -> {
                    buf.writeInt(packet.targetEntityId());
                    buf.writeResourceLocation(packet.bodyPartId());
                });
            }
        });
        context.setPacketHandled(true);
    }
}
