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

package cn.kuzuanpa.organapi.common.registry;

import cn.kuzuanpa.organapi.OrganApiMod;
import cn.kuzuanpa.organapi.common.block.SlaughterRoomBlock;
import cn.kuzuanpa.organapi.common.block.SurgeryRoomBlock;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.MapColor;
//? if forge {
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
//?} else {
/*import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
*///?}
import org.jetbrains.annotations.Nullable;

public final class OrganBlocks {
    //? if forge {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, OrganApiMod.MOD_ID);
    //?} else {
    /*public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, OrganApiMod.MOD_ID);
    *///?}

    public static final Supplier<Block> SURGERY_ROOM = BLOCKS.register("surgery_room",
            () -> new SurgeryRoomBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.5F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()));

    public static final Supplier<Block> SLAUGHTER_ROOM = BLOCKS.register("slaughter_room",
            () -> new SlaughterRoomBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.5F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()));

    public static final Supplier<Item> SURGERY_ROOM_ITEM = OrganItems.ITEMS.register("surgery_room",
            () -> new BlockItem(SURGERY_ROOM.get(), new Item.Properties()) {
                @Override
                //? if forge {
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                //?} else {
                /*public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                *///?}
                    tooltipComponents.add(Component.translatable("tooltip.organapi.surgery_room"));
                }
            });
    public static final Supplier<Item> SLAUGHTER_ROOM_ITEM = OrganItems.ITEMS.register("slaughter_room",
            () -> new BlockItem(SLAUGHTER_ROOM.get(), new Item.Properties()) {
                @Override
                //? if forge {
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
                //?} else {
                /*public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                *///?}
                    tooltipComponents.add(Component.translatable("tooltip.organapi.slaughter_room"));
                }
            });

    private OrganBlocks() {
    }
}
