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

import cn.kuzuanpa.organapi.OrganApiMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class OrganApiNetwork {
    private static final String PROTOCOL = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );
    private static int nextId;

    private OrganApiNetwork() {
    }

    public static void register() {
        CHANNEL.registerMessage(nextId++, SyncOrganDataS2CPacket.class,
                SyncOrganDataS2CPacket::encode,
                SyncOrganDataS2CPacket::decode,
                SyncOrganDataS2CPacket::handle);
        CHANNEL.registerMessage(nextId++, OpenOrganMenuC2SPacket.class,
                OpenOrganMenuC2SPacket::encode,
                OpenOrganMenuC2SPacket::decode,
                OpenOrganMenuC2SPacket::handle);
        CHANNEL.registerMessage(nextId++, CycleBodyPartC2SPacket.class,
                CycleBodyPartC2SPacket::encode,
                CycleBodyPartC2SPacket::decode,
                CycleBodyPartC2SPacket::handle);
        CHANNEL.registerMessage(nextId++, SelectBodyPartC2SPacket.class,
                SelectBodyPartC2SPacket::encode,
                SelectBodyPartC2SPacket::decode,
                SelectBodyPartC2SPacket::handle);
    }

    public static void sync(ServerPlayer viewer, Entity target) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> viewer), SyncOrganDataS2CPacket.from(target));
    }

    public static SimpleChannel channel() {
        return CHANNEL;
    }
}
