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
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if forge {
import net.minecraftforge.registries.DeferredRegister;
//?} else {
/*import net.neoforged.neoforge.registries.DeferredRegister;
*///?}

/**
 * Creative-tab registry. The tab itself is a plain {@code DeferredRegister} entry
 * (loader diff = package only, {@code //?}). The <em>content build</em> is a
 * loader event ({@code BuildCreativeModeTabContentsEvent}) whose accessor shape
 * differs, so the item list is exposed here as {@link #tabItems()} and each loader
 * entry ({@code OrganApiForge}/{@code OrganApiNeoForge}) subscribes its own event
 * and feeds this list — the event wiring is the structural coupling that goes to
 * the loader source set, the content itself stays common.
 */
public final class OrganCreativeTabs {
    //? if forge {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OrganApiMod.MOD_ID);
    //?} else {
    /*public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OrganApiMod.MOD_ID);
    *///?}

    public static final Supplier<CreativeModeTab> ORGAN_API = TABS.register("organ_api", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.organapi"))
            .icon(() -> new ItemStack(OrganItems.SCALPEL.get()))
            .build());

    /** The items shown in the organAPI creative tab, in display order. */
    public static List<Supplier<Item>> tabItems() {
        return List.of(
                OrganItems.CHEST_OPENER,
                OrganItems.ORGAN_POUCH,
                OrganItems.SCALPEL,
                OrganItems.SLAUGHTER_TOOL,
                OrganItems.DEBUG_HEART_INJECTOR,
                OrganBlocks.SURGERY_ROOM_ITEM,
                OrganBlocks.SLAUGHTER_ROOM_ITEM,
                OrganItems.SAMPLE_BRAIN,
                OrganItems.SAMPLE_HEART,
                OrganItems.SAMPLE_LUNG,
                OrganItems.SAMPLE_TENDON,
                OrganItems.SAMPLE_LEG_MUSCLE,
                OrganItems.CHEST_EXPANSION_KIT,
                OrganItems.LEG_EXPANSION_KIT,
                OrganItems.RIGHT_LEG_EXPANSION_KIT);
    }

    /** Feeds {@link #tabItems()} to a loader-supplied {@code accept} sink. */
    public static void buildContents(Consumer<ItemStack> accept) {
        for (Supplier<Item> item : tabItems()) {
            accept.accept(new ItemStack(item.get()));
        }
    }

    private OrganCreativeTabs() {
    }
}
