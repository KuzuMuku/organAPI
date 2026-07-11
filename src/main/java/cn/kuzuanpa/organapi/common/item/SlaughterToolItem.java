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
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SlaughterToolItem extends Item {
    public SlaughterToolItem(Properties properties) {
        super(properties);
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
