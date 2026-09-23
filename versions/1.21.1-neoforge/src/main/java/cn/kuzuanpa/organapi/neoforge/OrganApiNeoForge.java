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

package cn.kuzuanpa.organapi.neoforge;

import cn.kuzuanpa.organapi.OrganApiMod;
import cn.kuzuanpa.organapi.client.ClientSetup;
import cn.kuzuanpa.organapi.common.config.OrganApiConfig;
import cn.kuzuanpa.organapi.common.data.BodyPartDefinitionLoader;
import cn.kuzuanpa.organapi.common.data.BodyPlanDefinitionLoader;
import cn.kuzuanpa.organapi.common.data.OrganDefinitionLoader;
import cn.kuzuanpa.organapi.common.registry.OrganBlocks;
import cn.kuzuanpa.organapi.common.registry.OrganCreativeTabs;
import cn.kuzuanpa.organapi.common.registry.OrganItems;
import cn.kuzuanpa.organapi.common.registry.OrganMenus;
import cn.kuzuanpa.organapi.platform.Platform;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/**
 * NeoForge loader entry. Installs the NeoForge platform implementations
 * (attachment storage, payload network, DataComponent-aware item codec, NeoForge
 * menu/event seams), wires the mod-bus registries + payload/pack/reload events,
 * then defers shared setup to {@link OrganApiMod#init()}.
 */
@Mod(OrganApiMod.MOD_ID)
public final class OrganApiNeoForge {
    private final NeoForgeOrganNetwork network = new NeoForgeOrganNetwork();

    public OrganApiNeoForge(IEventBus modBus, ModContainer modContainer) {
        Platform.install(new NeoForgeOrganStorage(), network, new NeoForgeOrganItemCodec(),
                new NeoForgeOrganMenuAccess(), new NeoForgeOrganEvents());

        OrganApiMod.init();

        modContainer.registerConfig(ModConfig.Type.COMMON, OrganApiConfig.COMMON_SPEC);

        NeoForgeOrganAttachment.ATTACHMENT_TYPES.register(modBus);
        OrganItems.ITEMS.register(modBus);
        OrganBlocks.BLOCKS.register(modBus);
        OrganCreativeTabs.TABS.register(modBus);
        OrganMenus.MENU_TYPES.register(modBus);

        modBus.addListener(this::onRegisterPayloads);
        modBus.addListener(this::onBuildCreativeTab);
        modBus.addListener(this::onAddPackFinders);
        if (FMLEnvironment.dist.isClient()) {
            modBus.addListener(OrganApiNeoForgeClient::onRegisterScreens);
        }

        NeoForge.EVENT_BUS.addListener(this::onAddReloadListeners);
        NeoForge.EVENT_BUS.addListener(this::onPlayerLogin);
        NeoForge.EVENT_BUS.addListener(this::onPlayerRespawn);
        NeoForge.EVENT_BUS.addListener(this::onPlayerChangedDimension);
        NeoForge.EVENT_BUS.addListener(this::onPlayerClone);
    }

    private void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        network.registerPayloads(event);
    }

    private void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == OrganCreativeTabs.ORGAN_API.get()) {
            OrganCreativeTabs.buildContents(event::accept);
        }
    }

    private void onAddPackFinders(AddPackFindersEvent event) {
        event.addRepositorySource(new NeoForgeOrganPackLoader(event.getPackType()));
    }

    private void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(BodyPartDefinitionLoader.INSTANCE);
        event.addListener(BodyPlanDefinitionLoader.INSTANCE);
        event.addListener(OrganDefinitionLoader.INSTANCE);
    }

    private void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            Platform.network().sync(serverPlayer, serverPlayer);
        }
    }

    private void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            Platform.network().sync(serverPlayer, serverPlayer);
        }
    }

    private void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            Platform.network().sync(serverPlayer, serverPlayer);
        }
    }

    private void onPlayerClone(PlayerEvent.Clone event) {
        // The organ-holder attachment is copyOnDeath, so vanilla already carries it
        // across respawn; re-sync so the fresh client entity gets the snapshot.
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            Platform.network().sync(serverPlayer, serverPlayer);
        }
    }
}
