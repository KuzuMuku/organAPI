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

import cn.kuzuanpa.organapi.api.OrganApi;
import cn.kuzuanpa.organapi.api.install.OrganInstallResult;
import cn.kuzuanpa.organapi.common.registry.OrganItems;
import cn.kuzuanpa.organapi.core.model.BodyPartIds;
import cn.kuzuanpa.organapi.platform.OrganIds;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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

public class DebugHeartInjectorItem extends Item {
    public DebugHeartInjectorItem(Properties properties) {
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
        tooltipComponents.add(Component.translatable("tooltip.organapi.debug_heart_injector"));
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, Player player, @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        if (!player.level().isClientSide) {
            OrganInstallResult result = OrganApi.install(target, OrganIds.toResourceLocation(BodyPartIds.CHEST), new ItemStack(OrganItems.SAMPLE_HEART.get()));
            ChatFormatting color = result.success() ? ChatFormatting.GREEN : ChatFormatting.RED;
            player.displayClientMessage(result.message().copy().withStyle(color), true);
        }
        return InteractionResult.sidedSuccess(player.level().isClientSide);
    }
}
