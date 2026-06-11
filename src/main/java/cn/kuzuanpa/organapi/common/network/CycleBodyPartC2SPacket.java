package cn.kuzuanpa.organapi.common.network;

import cn.kuzuanpa.organapi.common.menu.OrganMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record CycleBodyPartC2SPacket(int direction) {
    public static void encode(CycleBodyPartC2SPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.direction);
    }

    public static CycleBodyPartC2SPacket decode(FriendlyByteBuf buf) {
        return new CycleBodyPartC2SPacket(buf.readInt());
    }

    public static void handle(CycleBodyPartC2SPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof OrganMenu organMenu) {
                organMenu.cycleBodyPart(packet.direction);
            }
        });
        context.setPacketHandled(true);
    }
}
