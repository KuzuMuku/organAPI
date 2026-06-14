package cn.kuzuanpa.organapi.common.item;

import cn.kuzuanpa.organapi.api.OrganApi;
import cn.kuzuanpa.organapi.api.body.BodyPartIds;
import cn.kuzuanpa.organapi.api.install.OrganInstallResult;
import cn.kuzuanpa.organapi.common.registry.OrganItems;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DebugHeartInjectorItem extends Item {
    public DebugHeartInjectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, Player player, @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        if (!player.level().isClientSide) {
            OrganInstallResult result = OrganApi.install(target, BodyPartIds.CHEST, new ItemStack(OrganItems.SAMPLE_HEART.get()));
            ChatFormatting color = result.success() ? ChatFormatting.GREEN : ChatFormatting.RED;
            player.displayClientMessage(result.message().copy().withStyle(color), true);
        }
        return InteractionResult.sidedSuccess(player.level().isClientSide);
    }
}
