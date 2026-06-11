package cn.kuzuanpa.organapi.common.network;

import cn.kuzuanpa.organapi.common.capability.IOrganHolder;
import cn.kuzuanpa.organapi.common.capability.PlayerOrganHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SyncOrganDataS2CPacket(int entityId, CompoundTag data) {
    public static SyncOrganDataS2CPacket from(Entity entity) {
        CompoundTag tag = IOrganHolder.resolve(entity)
                .filter(holder -> holder instanceof PlayerOrganHolder)
                .map(holder -> ((PlayerOrganHolder) holder).serializeNBT())
                .orElseGet(CompoundTag::new);
        return new SyncOrganDataS2CPacket(entity.getId(), tag);
    }

    public static void encode(SyncOrganDataS2CPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.entityId);
        buf.writeNbt(packet.data);
    }

    public static SyncOrganDataS2CPacket decode(FriendlyByteBuf buf) {
        return new SyncOrganDataS2CPacket(buf.readInt(), buf.readNbt());
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
                IOrganHolder.resolve(entity)
                        .filter(holder -> holder instanceof PlayerOrganHolder)
                        .ifPresent(holder -> ((PlayerOrganHolder) holder).deserializeNBT(packet.data));
            }
        });
        context.setPacketHandled(true);
    }
}
