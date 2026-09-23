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
import cn.kuzuanpa.organapi.common.menu.BodyPartSelectionMenu;
import cn.kuzuanpa.organapi.common.menu.OrganMenu;
import cn.kuzuanpa.organapi.common.menu.OrganOverviewMenu;
import cn.kuzuanpa.organapi.common.util.OrganDataKeys;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
//? if forge {
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
//?} else {
/*import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
*///?}

/**
 * Menu-type registry. Two loader diffs, both {@code //?}: the {@code MenuType}
 * factory helper is {@code IForgeMenuType} on Forge vs {@code IMenuTypeExtension}
 * on NeoForge (same {@code create(IContainerFactory)} shape; the extra-data buf is
 * a {@code FriendlyByteBuf} on Forge and a {@code RegistryFriendlyByteBuf} on
 * NeoForge, but {@code readInt}/{@code readResourceLocation} exist on both), and
 * the {@code DeferredRegister} package/key.
 */
public final class OrganMenus {
    //? if forge {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, OrganApiMod.MOD_ID);
    //?} else {
    /*public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, OrganApiMod.MOD_ID);
    *///?}

    public static final Supplier<MenuType<OrganMenu>> ORGAN_MENU = MENU_TYPES.register("organ_menu",
            //? if forge {
            () -> IForgeMenuType.create((windowId, inventory, data) -> new OrganMenu(windowId, inventory,
            //?} else {
            /*() -> IMenuTypeExtension.create((windowId, inventory, data) -> new OrganMenu(windowId, inventory,
            *///?}
                    data.readableBytes() > 4 ? data.readInt() : inventory.player.getId(),
                    data.readableBytes() > 0 ? data.readResourceLocation() : OrganDataKeys.DEFAULT_BODY_PART)));
    public static final Supplier<MenuType<BodyPartSelectionMenu>> BODY_PART_SELECTION_MENU = MENU_TYPES.register("body_part_selection",
            //? if forge {
            () -> IForgeMenuType.create((windowId, inventory, data) -> new BodyPartSelectionMenu(windowId, inventory,
            //?} else {
            /*() -> IMenuTypeExtension.create((windowId, inventory, data) -> new BodyPartSelectionMenu(windowId, inventory,
            *///?}
                    data.readableBytes() > 0 ? data.readInt() : inventory.player.getId())));
    public static final Supplier<MenuType<OrganOverviewMenu>> ORGAN_OVERVIEW_MENU = MENU_TYPES.register("organ_overview",
            //? if forge {
            () -> IForgeMenuType.create((windowId, inventory, data) -> new OrganOverviewMenu(windowId, inventory,
            //?} else {
            /*() -> IMenuTypeExtension.create((windowId, inventory, data) -> new OrganOverviewMenu(windowId, inventory,
            *///?}
                    data.readableBytes() > 4 ? data.readInt() : inventory.player.getId(),
                    data.readableBytes() > 0 ? data.readResourceLocation() : ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "head"))));

    private OrganMenus() {
    }
}
