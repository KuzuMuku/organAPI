package cn.kuzuanpa.organapi;

import cn.kuzuanpa.organapi.client.ClientSetup;
import cn.kuzuanpa.organapi.common.data.BodyPartDefinitionLoader;
import cn.kuzuanpa.organapi.common.data.OrganDefinitionLoader;
import cn.kuzuanpa.organapi.common.event.CommonForgeEvents;
import cn.kuzuanpa.organapi.common.network.OrganApiNetwork;
import cn.kuzuanpa.organapi.common.registry.OrganBlocks;
import cn.kuzuanpa.organapi.common.registry.OrganItems;
import cn.kuzuanpa.organapi.common.registry.OrganMenus;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(OrganApiMod.MOD_ID)
public class OrganApiMod {
    public static final String MOD_ID = "organapi";

    public OrganApiMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        OrganItems.ITEMS.register(modBus);
        OrganBlocks.BLOCKS.register(modBus);
        OrganMenus.MENU_TYPES.register(modBus);
        modBus.addListener(this::onClientSetup);

        OrganApiNetwork.register();
        MinecraftForge.EVENT_BUS.register(new CommonForgeEvents());
        MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListeners);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(ClientSetup::registerScreens);
    }

    private void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(BodyPartDefinitionLoader.INSTANCE);
        event.addListener(OrganDefinitionLoader.INSTANCE);
    }
}
