package cn.kuzuanpa.organapi.common.network;

import cn.kuzuanpa.organapi.common.menu.OrganMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public record OpenOrganMenuC2SPacket(ResourceLocation bodyPartId) {
    public static void encode(OpenOrganMenuC2SPacket packet, FriendlyByteBuf buf) {
        buf.writeResourceLocation(packet.bodyPartId);
    }

    public static OpenOrganMenuC2SPacket decode(FriendlyByteBuf buf) {
        return new OpenOrganMenuC2SPacket(buf.readResourceLocation());
    }

    public static void handle(OpenOrganMenuC2SPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                NetworkHooks.openScreen(player, new SimpleMenuProvider(
                        (windowId, inventory, entityPlayer) -> new OrganMenu(windowId, inventory, packet.bodyPartId()),
                        Component.translatable("menu.organapi.organ_menu")
                ), buf -> buf.writeResourceLocation(packet.bodyPartId()));
            }
        });
        context.setPacketHandled(true);
    }
}
