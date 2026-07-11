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
import org.jetbrains.annotations.NotNull;

public class BodyExpansionItem extends Item {
    private final ResourceLocation bodyPartId;
    private final int amount;

    public BodyExpansionItem(Properties properties, ResourceLocation bodyPartId, int amount) {
        super(properties);
        this.bodyPartId = bodyPartId;
        this.amount = amount;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            boolean success = OrganApi.addCapacity(player, bodyPartId, amount);
            if (success) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                if (player instanceof ServerPlayer serverPlayer) {
                    OrganApiNetwork.sync(serverPlayer, serverPlayer);
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
