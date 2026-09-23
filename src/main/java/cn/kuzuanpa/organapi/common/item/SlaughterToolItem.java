/*
 * This class was created by <kuzuanpa>. It is distributed as
 * part of the organAPI Mod. Get the Source Code in github:
 * https://github.com/KuzuMuku/organAPI
 *
 * organAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.

 * organAPI is Open Source and distributed under the
 * AGPLv3 License: https://www.gnu.org/licenses/agpl-3.0.txt
 *
 */

package cn.kuzuanpa.organapi.common.item;

import cn.kuzuanpa.organapi.common.util.SlaughterAccessHelper;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SlaughterToolItem extends Item {
    public SlaughterToolItem(Properties properties) {
        super(properties);
    }

    @Override
    //? if forge {
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    //?} else {
    /*public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    *///?}
        tooltipComponents.add(Component.translatable("tooltip.organapi.slaughter_tool"));
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, Player player, @NotNull LivingEntity interactionTarget, @NotNull InteractionHand usedHand) {
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
