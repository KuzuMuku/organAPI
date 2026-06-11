package cn.kuzuanpa.organapi.common.item;

import cn.kuzuanpa.organapi.api.OrganApi;
import cn.kuzuanpa.organapi.common.menu.OrganMenu;
import cn.kuzuanpa.organapi.common.menu.OrganOverviewMenu;
import cn.kuzuanpa.organapi.common.network.OrganApiNetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BodyExpansionItem extends Item {
    private final ResourceLocation bodyPartId;
    private final int amount;

    public BodyExpansionItem(Properties properties, ResourceLocation bodyPartId, int amount) {
        super(properties);
        this.bodyPartId = bodyPartId;
        this.amount = amount;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            boolean success = OrganApi.addCapacity(player, bodyPartId, amount);
            if (success) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                if (player instanceof ServerPlayer serverPlayer) {
                    OrganApiNetwork.sync(serverPlayer);
                    if (serverPlayer.containerMenu instanceof OrganMenu organMenu) {
                        organMenu.broadcastChanges();
                    }
                    if (serverPlayer.containerMenu instanceof OrganOverviewMenu organOverviewMenu) {
                        organOverviewMenu.broadcastChanges();
                    }
                }
                player.displayClientMessage(Component.translatable("message.organapi.capacity_added", amount,
                        Component.translatable("body_part." + bodyPartId.getNamespace() + "." + bodyPartId.getPath())).withStyle(ChatFormatting.GREEN), true);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
