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
import cn.kuzuanpa.organapi.api.body.BodyPartIds;
import cn.kuzuanpa.organapi.common.item.BodyExpansionItem;
import cn.kuzuanpa.organapi.common.item.DebugHeartInjectorItem;
import cn.kuzuanpa.organapi.common.item.OrganItem;
import cn.kuzuanpa.organapi.common.item.OrganPouchItem;
import cn.kuzuanpa.organapi.common.item.ScalpelItem;
import cn.kuzuanpa.organapi.common.item.SlaughterToolItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class OrganItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, OrganApiMod.MOD_ID);

    public static final RegistryObject<Item> CHEST_OPENER = ITEMS.register("chest_opener", () -> new OrganPouchItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ORGAN_POUCH = ITEMS.register("organ_pouch", () -> new OrganPouchItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SCALPEL = ITEMS.register("scalpel", () -> new ScalpelItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SLAUGHTER_TOOL = ITEMS.register("slaughter_tool", () -> new SlaughterToolItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DEBUG_HEART_INJECTOR = ITEMS.register("debug_heart_injector", () -> new DebugHeartInjectorItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SAMPLE_BRAIN = ITEMS.register("sample_brain",
            () -> new OrganItem(new Item.Properties().stacksTo(1), ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "sample_brain")));
    public static final RegistryObject<Item> SAMPLE_HEART = ITEMS.register("sample_heart",
            () -> new OrganItem(new Item.Properties().stacksTo(1), ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "sample_heart")));
    public static final RegistryObject<Item> SAMPLE_LUNG = ITEMS.register("sample_lung",
            () -> new OrganItem(new Item.Properties().stacksTo(1), ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "sample_lung")));
    public static final RegistryObject<Item> SAMPLE_TENDON = ITEMS.register("sample_tendon",
            () -> new OrganItem(new Item.Properties().stacksTo(1), ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "sample_tendon")));
    public static final RegistryObject<Item> SAMPLE_LEG_MUSCLE = ITEMS.register("sample_leg_muscle",
            () -> new OrganItem(new Item.Properties().stacksTo(1), ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "sample_leg_muscle")));
    public static final RegistryObject<Item> CHEST_EXPANSION_KIT = ITEMS.register("chest_expansion_kit",
            () -> new BodyExpansionItem(new Item.Properties().stacksTo(16), BodyPartIds.CHEST, 1));
    public static final RegistryObject<Item> LEG_EXPANSION_KIT = ITEMS.register("leg_expansion_kit",
            () -> new BodyExpansionItem(new Item.Properties().stacksTo(16), BodyPartIds.LEFT_LEG, 1));
    public static final RegistryObject<Item> RIGHT_LEG_EXPANSION_KIT = ITEMS.register("right_leg_expansion_kit",
            () -> new BodyExpansionItem(new Item.Properties().stacksTo(16), BodyPartIds.RIGHT_LEG, 1));

    private OrganItems() {
    }
}
