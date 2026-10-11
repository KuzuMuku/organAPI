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
import cn.kuzuanpa.organapi.common.item.BodyExpansionItem;
import cn.kuzuanpa.organapi.common.item.DebugHeartInjectorItem;
import cn.kuzuanpa.organapi.common.item.OrganItem;
import cn.kuzuanpa.organapi.common.item.OrganPouchItem;
import cn.kuzuanpa.organapi.common.item.ScalpelItem;
import cn.kuzuanpa.organapi.common.item.SlaughterToolItem;
import cn.kuzuanpa.organapi.core.model.BodyPartIds;
import cn.kuzuanpa.organapi.platform.OrganIds;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if forge {
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
//?} else {
/*import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
*///?}

/**
 * Item registry holder. The {@code DeferredRegister} concept is identical on both
 * loaders; only its package and the registry key it is created from differ (a
 * minor signature diff handled with {@code //?}). Holder fields are typed as
 * {@link Supplier} because Forge's {@code RegistryObject} and NeoForge's
 * {@code DeferredHolder} both implement it, so all downstream {@code .get()} calls
 * are loader-neutral. The mod-bus wiring itself lives in {@code OrganRegistrar}.
 */
public final class OrganItems {
    //? if forge {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, OrganApiMod.MOD_ID);
    //?} else {
    /*public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, OrganApiMod.MOD_ID);
    *///?}

    public static final Supplier<Item> CHEST_OPENER = ITEMS.register("chest_opener", () -> new OrganPouchItem(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> ORGAN_POUCH = ITEMS.register("organ_pouch", () -> new OrganPouchItem(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> SCALPEL = ITEMS.register("scalpel", () -> new ScalpelItem(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> SLAUGHTER_TOOL = ITEMS.register("slaughter_tool", () -> new SlaughterToolItem(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> DEBUG_HEART_INJECTOR = ITEMS.register("debug_heart_injector", () -> new DebugHeartInjectorItem(new Item.Properties().stacksTo(1)));
    /**
     * Single generic organ carrier. {@code definitionId == null} means the organ id
     * is read per-stack via the {@code OrganItemIdentity} seam, so every
     * data-defined organ (theOrgans content) rides on this one registered item —
     * the tacz {@code modern_kinetic_gun} pattern. Concrete organs are data, not
     * registry entries.
     */
    public static final Supplier<Item> ORGAN_ITEM = ITEMS.register("organ_item",
            () -> new OrganItem(new Item.Properties().stacksTo(1), null));
    public static final Supplier<Item> SAMPLE_BRAIN = ITEMS.register("sample_brain",
            () -> new OrganItem(new Item.Properties().stacksTo(1), ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "sample_brain")));
    public static final Supplier<Item> SAMPLE_HEART = ITEMS.register("sample_heart",
            () -> new OrganItem(new Item.Properties().stacksTo(1), ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "sample_heart")));
    public static final Supplier<Item> SAMPLE_LUNG = ITEMS.register("sample_lung",
            () -> new OrganItem(new Item.Properties().stacksTo(1), ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "sample_lung")));
    public static final Supplier<Item> SAMPLE_TENDON = ITEMS.register("sample_tendon",
            () -> new OrganItem(new Item.Properties().stacksTo(1), ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "sample_tendon")));
    public static final Supplier<Item> SAMPLE_LEG_MUSCLE = ITEMS.register("sample_leg_muscle",
            () -> new OrganItem(new Item.Properties().stacksTo(1), ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "sample_leg_muscle")));
    public static final Supplier<Item> CHEST_EXPANSION_KIT = ITEMS.register("chest_expansion_kit",
            () -> new BodyExpansionItem(new Item.Properties().stacksTo(16), OrganIds.toResourceLocation(BodyPartIds.CHEST), 1));
    public static final Supplier<Item> LEG_EXPANSION_KIT = ITEMS.register("leg_expansion_kit",
            () -> new BodyExpansionItem(new Item.Properties().stacksTo(16), OrganIds.toResourceLocation(BodyPartIds.LEFT_LEG), 1));
    public static final Supplier<Item> RIGHT_LEG_EXPANSION_KIT = ITEMS.register("right_leg_expansion_kit",
            () -> new BodyExpansionItem(new Item.Properties().stacksTo(16), OrganIds.toResourceLocation(BodyPartIds.RIGHT_LEG), 1));

    private OrganItems() {
    }

    /**
     * Builds a generic {@link #ORGAN_ITEM} stack stamped with {@code organId} via the
     * {@code OrganItemIdentity} seam (forge NBT / neoforge DataComponent). This is the
     * single factory every obtain path shares — creative tab, loot ({@code set_organ_id}
     * loot function), surgery recipe results, and default-loadout spawning — so data-driven
     * organs (which register no item of their own) can be produced outside the creative tab.
     */
    public static ItemStack createOrganStack(ResourceLocation organId) {
        ItemStack stack = new ItemStack(ORGAN_ITEM.get());
        cn.kuzuanpa.organapi.platform.Platform.itemIdentity().setOrganId(stack, organId);
        return stack;
    }
}
