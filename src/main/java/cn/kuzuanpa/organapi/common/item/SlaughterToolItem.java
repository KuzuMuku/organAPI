package cn.kuzuanpa.organapi.common.item;

import cn.kuzuanpa.organapi.common.util.SlaughterAccessHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SlaughterToolItem extends Item {
    public SlaughterToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (!SlaughterAccessHelper.canOpenChestCavity(interactionTarget)) {
                player.displayClientMessage(Component.translatable("message.organapi.target_too_healthy"), true);
                return InteractionResult.CONSUME;
            }
            SlaughterAccessHelper.applyOpenedChestRestriction(interactionTarget);
            SlaughterAccessHelper.openOverview(serverPlayer, interactionTarget);
        }
        return InteractionResult.sidedSuccess(player.level().isClientSide);
    }
}
