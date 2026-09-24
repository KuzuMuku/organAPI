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

package cn.kuzuanpa.organapi.forge;

import cn.kuzuanpa.organapi.OrganApiMod;
import cn.kuzuanpa.organapi.client.ClientSetup;
import cn.kuzuanpa.organapi.common.capability.ForgeOrganStorage;
import cn.kuzuanpa.organapi.common.config.OrganApiConfig;
import cn.kuzuanpa.organapi.common.data.BodyPartDefinitionLoader;
import cn.kuzuanpa.organapi.common.data.BodyPlanDefinitionLoader;
import cn.kuzuanpa.organapi.common.data.OrganDefinitionLoader;
import cn.kuzuanpa.organapi.common.data.pack.OrganPackLoader;
import cn.kuzuanpa.organapi.common.network.OrganApiNetwork;
import cn.kuzuanpa.organapi.common.registry.OrganBlocks;
import cn.kuzuanpa.organapi.common.registry.OrganCreativeTabs;
import cn.kuzuanpa.organapi.common.registry.OrganItems;
import cn.kuzuanpa.organapi.common.registry.OrganMenus;
import cn.kuzuanpa.organapi.platform.Platform;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Forge loader entry. Installs the Forge platform implementations, wires the
 * content/network/reload/pack lifecycle onto the Forge buses, then defers shared
 * setup to {@link OrganApiMod#init()}.
 */
@Mod(OrganApiMod.MOD_ID)
public final class OrganApiForge {
    public OrganApiForge(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();

        ForgeOrganStorage storage = new ForgeOrganStorage();
        OrganApiNetwork network = new OrganApiNetwork();
        Platform.install(storage, network, new ForgeOrganItemCodec(),
                new ForgeOrganMenuAccess(), new ForgeOrganEvents(), new ForgeOrganItemIdentity());

        OrganApiMod.init();

        context.registerConfig(ModConfig.Type.COMMON, OrganApiConfig.COMMON_SPEC);
        OrganItems.ITEMS.register(modBus);
        OrganBlocks.BLOCKS.register(modBus);
        OrganCreativeTabs.TABS.register(modBus);
        OrganMenus.MENU_TYPES.register(modBus);
        modBus.addListener(this::onClientSetup);
        modBus.addListener(this::onBuildCreativeTab);
        modBus.addListener(this::onAddPackFinders);
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist.isClient()) {
            modBus.addListener(cn.kuzuanpa.organapi.forge.client.ForgeOrganItemModelSetup::onRegisterAdditional);
            modBus.addListener(cn.kuzuanpa.organapi.forge.client.ForgeOrganItemModelSetup::onBakingCompleted);
        }

        network.register();
        storage.register();
        MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListeners);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(ClientSetup::registerScreens);
    }

    private void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == OrganCreativeTabs.ORGAN_API.get()) {
            OrganCreativeTabs.buildContents(event::accept);
        }
    }

    private void onAddPackFinders(AddPackFindersEvent event) {
        event.addRepositorySource(new OrganPackLoader(event.getPackType()));
    }

    private void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(BodyPartDefinitionLoader.INSTANCE);
        event.addListener(BodyPlanDefinitionLoader.INSTANCE);
        event.addListener(OrganDefinitionLoader.INSTANCE);
    }
}
