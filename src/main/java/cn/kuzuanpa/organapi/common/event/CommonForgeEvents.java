package cn.kuzuanpa.organapi.common.event;

import cn.kuzuanpa.organapi.common.capability.IOrganHolder;
import cn.kuzuanpa.organapi.common.capability.OrganHolderProvider;
import cn.kuzuanpa.organapi.common.network.OrganApiNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonForgeEvents {
    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        event.addCapability(OrganHolderProvider.ID, new OrganHolderProvider(event.getObject()));
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        IOrganHolder.resolve(event.getOriginal()).ifPresent(original ->
                IOrganHolder.resolve(event.getEntity()).ifPresent(copy -> copy.copyFrom(original)));
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public void onPlayerLogin(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            OrganApiNetwork.sync(serverPlayer, serverPlayer);
        }
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            OrganApiNetwork.sync(serverPlayer, serverPlayer);
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            OrganApiNetwork.sync(serverPlayer, serverPlayer);
        }
    }
}
