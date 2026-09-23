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

package cn.kuzuanpa.organapi.neoforge.network;

import cn.kuzuanpa.organapi.OrganApiMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * S2C organ snapshot payload — the NeoForge equivalent of the Forge
 * {@code SyncOrganDataS2CPacket}. The body is the identical loader-independent
 * {@link cn.kuzuanpa.organapi.core.state.OrganStateCodec} byte array (entity id +
 * bytes), so the wire format matches the Forge side exactly.
 */
public record SyncOrganDataPayload(int entityId, byte[] data) implements CustomPacketPayload {
    public static final Type<SyncOrganDataPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "sync_organ_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncOrganDataPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, SyncOrganDataPayload::entityId,
                    ByteBufCodecs.BYTE_ARRAY, SyncOrganDataPayload::data,
                    SyncOrganDataPayload::new);

    @Override
    public Type<SyncOrganDataPayload> type() {
        return TYPE;
    }
}
