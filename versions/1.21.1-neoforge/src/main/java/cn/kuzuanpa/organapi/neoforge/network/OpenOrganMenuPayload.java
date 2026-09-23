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
 * C2S: open the per-body-part organ menu (NeoForge equivalent of the Forge
 * {@code OpenOrganMenuC2SPacket}). Carries a target entity id and a body-part id.
 */
public record OpenOrganMenuPayload(int targetEntityId, ResourceLocation bodyPartId) implements CustomPacketPayload {
    public static final Type<OpenOrganMenuPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "open_organ_menu"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenOrganMenuPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, OpenOrganMenuPayload::targetEntityId,
                    ResourceLocation.STREAM_CODEC, OpenOrganMenuPayload::bodyPartId,
                    OpenOrganMenuPayload::new);

    @Override
    public Type<OpenOrganMenuPayload> type() {
        return TYPE;
    }
}
