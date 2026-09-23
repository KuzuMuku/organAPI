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

import cn.kuzuanpa.organapi.OrganApiMod;
import cn.kuzuanpa.organapi.common.capability.IOrganHolder;
import cn.kuzuanpa.organapi.common.menu.OrganMenu;
import cn.kuzuanpa.organapi.common.menu.OrganOverviewMenu;
import cn.kuzuanpa.organapi.neoforge.network.OpenOrganMenuPayload;
import cn.kuzuanpa.organapi.neoforge.network.SelectBodyPartPayload;
import cn.kuzuanpa.organapi.neoforge.network.SyncOrganDataPayload;
import cn.kuzuanpa.organapi.platform.OrganNetwork;
import cn.kuzuanpa.organapi.platform.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * NeoForge implementation of {@link OrganNetwork} using the 1.21.1 payload
 * registrar ({@code CustomPacketPayload} + {@code StreamCodec}) instead of the
 * Forge {@code SimpleChannel}. The S2C snapshot body is the identical
 * loader-independent codec {@code byte[]}.
 *
 * <p>Payload handlers run on the network thread, so every world-state mutation is
 * wrapped in {@code context.enqueueWork(...)} to hop to the main thread (matching
 * the Forge {@code enqueueWork} behavior).
 */
public final class NeoForgeOrganNetwork implements OrganNetwork {
    private static final String PROTOCOL = "1";

    @Override
    public void register() {
        // Payloads are registered from the RegisterPayloadHandlersEvent (see the
        // entry class); the OrganNetwork#register() seam is a no-op on NeoForge.
    }

    /** Wires all payload types + handlers onto the loader's registrar. */
    public void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(PROTOCOL);
        registrar.playToClient(SyncOrganDataPayload.TYPE, SyncOrganDataPayload.CODEC,
                NeoForgeOrganNetwork::handleSync);
        registrar.playToServer(OpenOrganMenuPayload.TYPE, OpenOrganMenuPayload.CODEC,
                NeoForgeOrganNetwork::handleOpenMenu);
        registrar.playToServer(SelectBodyPartPayload.TYPE, SelectBodyPartPayload.CODEC,
                NeoForgeOrganNetwork::handleSelectBodyPart);
    }

    @Override
    public void sync(ServerPlayer viewer, Entity target) {
        PacketDistributor.sendToPlayer(viewer, new SyncOrganDataPayload(target.getId(),
                Platform.storage().serialize(target)));
    }

    @Override
    public void sendOpenOrganMenu(int targetEntityId, ResourceLocation bodyPartId) {
        PacketDistributor.sendToServer(new OpenOrganMenuPayload(targetEntityId, bodyPartId));
    }

    @Override
    public void sendSelectBodyPart(int bodyPartIndex) {
        PacketDistributor.sendToServer(new SelectBodyPartPayload(bodyPartIndex));
    }

    private static void handleSync(SyncOrganDataPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.level == null) {
                return;
            }
            Entity entity = minecraft.level.getEntity(payload.entityId());
            if (entity != null) {
                Platform.storage().apply(entity, payload.data());
            }
        });
    }

    private static void handleOpenMenu(OpenOrganMenuPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            Entity target = player.level().getEntity(payload.targetEntityId());
            if (target == null) {
                return;
            }
            Platform.menus().openMenu(player, new SimpleMenuProvider(
                    (windowId, inventory, entityPlayer) -> new OrganMenu(windowId, inventory, payload.targetEntityId(), payload.bodyPartId()),
                    Component.translatable("menu.organapi.organ_menu")
            ), buf -> {
                buf.writeInt(payload.targetEntityId());
                buf.writeResourceLocation(payload.bodyPartId());
            });
        });
    }

    private static void handleSelectBodyPart(SelectBodyPartPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player
                    && player.containerMenu instanceof OrganOverviewMenu overviewMenu) {
                IOrganHolder.resolve(overviewMenu.getTarget()).ifPresent(IOrganHolder::markDirty);
                overviewMenu.setSelectedBodyPartIndex(payload.bodyPartIndex());
            }
        });
    }
}
