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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class OrganBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, OrganApiMod.MOD_ID);

    public static final RegistryObject<Block> SURGERY_ROOM = BLOCKS.register("surgery_room",
            () -> new SurgeryRoomBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.5F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> SLAUGHTER_ROOM = BLOCKS.register("slaughter_room",
            () -> new SlaughterRoomBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.5F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Item> SURGERY_ROOM_ITEM = OrganItems.ITEMS.register("surgery_room",
            () -> new BlockItem(SURGERY_ROOM.get(), new Item.Properties()));
    public static final RegistryObject<Item> SLAUGHTER_ROOM_ITEM = OrganItems.ITEMS.register("slaughter_room",
            () -> new BlockItem(SLAUGHTER_ROOM.get(), new Item.Properties()));

    private OrganBlocks() {
    }
}
