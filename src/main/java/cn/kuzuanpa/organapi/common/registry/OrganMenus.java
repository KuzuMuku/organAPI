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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class OrganMenus {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, OrganApiMod.MOD_ID);

    public static final RegistryObject<MenuType<OrganMenu>> ORGAN_MENU = MENU_TYPES.register("organ_menu",
            () -> IForgeMenuType.create((windowId, inventory, data) -> new OrganMenu(windowId, inventory,
                    data.readableBytes() > 4 ? data.readInt() : inventory.player.getId(),
                    data.readableBytes() > 0 ? data.readResourceLocation() : OrganDataKeys.DEFAULT_BODY_PART)));
    public static final RegistryObject<MenuType<BodyPartSelectionMenu>> BODY_PART_SELECTION_MENU = MENU_TYPES.register("body_part_selection",
            () -> IForgeMenuType.create((windowId, inventory, data) -> new BodyPartSelectionMenu(windowId, inventory,
                    data.readableBytes() > 0 ? data.readInt() : inventory.player.getId())));
    public static final RegistryObject<MenuType<OrganOverviewMenu>> ORGAN_OVERVIEW_MENU = MENU_TYPES.register("organ_overview",
            () -> IForgeMenuType.create((windowId, inventory, data) -> new OrganOverviewMenu(windowId, inventory,
                    data.readableBytes() > 4 ? data.readInt() : inventory.player.getId(),
                    data.readableBytes() > 0 ? data.readResourceLocation() : ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "head"))));

    private OrganMenus() {
    }
}
