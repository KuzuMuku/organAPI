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
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = OrganApiMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class OrganCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OrganApiMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> ORGAN_API = TABS.register("organ_api", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.organapi"))
            .icon(() -> new ItemStack(OrganItems.SCALPEL.get()))
            .build());

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() != ORGAN_API.get()) {
            return;
        }
        event.accept(OrganItems.CHEST_OPENER.get());
        event.accept(OrganItems.ORGAN_POUCH.get());
        event.accept(OrganItems.SCALPEL.get());
        event.accept(OrganItems.SLAUGHTER_TOOL.get());
        event.accept(OrganItems.DEBUG_HEART_INJECTOR.get());
        event.accept(OrganBlocks.SURGERY_ROOM_ITEM.get());
        event.accept(OrganBlocks.SLAUGHTER_ROOM_ITEM.get());
        event.accept(OrganItems.SAMPLE_BRAIN.get());
        event.accept(OrganItems.SAMPLE_HEART.get());
        event.accept(OrganItems.SAMPLE_LUNG.get());
        event.accept(OrganItems.SAMPLE_TENDON.get());
        event.accept(OrganItems.SAMPLE_LEG_MUSCLE.get());
        event.accept(OrganItems.CHEST_EXPANSION_KIT.get());
        event.accept(OrganItems.LEG_EXPANSION_KIT.get());
        event.accept(OrganItems.RIGHT_LEG_EXPANSION_KIT.get());
    }

    private OrganCreativeTabs() {
    }
}
