package cn.kuzuanpa.organapi.common.network;

import cn.kuzuanpa.organapi.common.capability.IOrganHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SyncOrganDataS2CPacket(CompoundTag data) {
    public static SyncOrganDataS2CPacket from(Player player) {
        CompoundTag tag = IOrganHolder.resolve(player)
                .filter(holder -> holder instanceof cn.kuzuanpa.organapi.common.capability.PlayerOrganHolder)
                .map(holder -> ((cn.kuzuanpa.organapi.common.capability.PlayerOrganHolder) holder).serializeNBT())
                .orElseGet(CompoundTag::new);
        return new SyncOrganDataS2CPacket(tag);
    }

    public static void encode(SyncOrganDataS2CPacket packet, FriendlyByteBuf buf) {
        buf.writeNbt(packet.data);
    }

    public static SyncOrganDataS2CPacket decode(FriendlyByteBuf buf) {
        return new SyncOrganDataS2CPacket(buf.readNbt());
    }

    public static void handle(SyncOrganDataS2CPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                IOrganHolder.resolve(player)
                        .filter(holder -> holder instanceof cn.kuzuanpa.organapi.common.capability.PlayerOrganHolder)
                        .ifPresent(holder -> ((cn.kuzuanpa.organapi.common.capability.PlayerOrganHolder) holder).deserializeNBT(packet.data));
            }
        });
        context.setPacketHandled(true);
    }
}
