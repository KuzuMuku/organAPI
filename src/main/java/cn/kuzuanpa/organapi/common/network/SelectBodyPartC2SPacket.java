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
            IOrganHolder.resolve(player).ifPresent(IOrganHolder::markDirty);

            if (player != null && player.containerMenu instanceof OrganOverviewMenu organOverviewMenu) {
                organOverviewMenu.setSelectedBodyPartIndex(packet.bodyPartIndex());
            }
        });
        context.setPacketHandled(true);
    }
}
