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

import cn.kuzuanpa.organapi.platform.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Server→client organ sync packet. The payload is the loader-independent
 * {@code OrganStateData} codec byte array produced by
 * {@link Platform#storage()}, not a {@code CompoundTag}, so step 4's NeoForge
 * payload port reuses the exact same bytes.
 */
public record SyncOrganDataS2CPacket(int entityId, byte[] data) {
    public static SyncOrganDataS2CPacket from(Entity entity) {
        return new SyncOrganDataS2CPacket(entity.getId(), Platform.storage().serialize(entity));
    }

    public static void encode(SyncOrganDataS2CPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.entityId);
        buf.writeByteArray(packet.data);
    }

    public static SyncOrganDataS2CPacket decode(FriendlyByteBuf buf) {
        return new SyncOrganDataS2CPacket(buf.readInt(), buf.readByteArray());
    }

    public static void handle(SyncOrganDataS2CPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.level == null) {
                return;
            }
            Entity entity = minecraft.level.getEntity(packet.entityId());
            if (entity != null) {
                Platform.storage().apply(entity, packet.data());
            }
        });
        context.setPacketHandled(true);
    }
}
