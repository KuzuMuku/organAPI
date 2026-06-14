package cn.kuzuanpa.organapi.common.item;

import cn.kuzuanpa.organapi.common.menu.BodyPartSelectionMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class OrganPouchItem extends Item {
    public OrganPouchItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider(
                    (windowId, inventory, entityPlayer) -> new BodyPartSelectionMenu(windowId, inventory, serverPlayer.getId()),
                    Component.translatable("menu.organapi.body_part_selection")), buf -> buf.writeInt(serverPlayer.getId()));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
