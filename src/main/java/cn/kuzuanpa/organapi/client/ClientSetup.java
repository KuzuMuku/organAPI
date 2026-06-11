package cn.kuzuanpa.organapi.client;

import cn.kuzuanpa.organapi.client.screen.BodyPartSelectionScreen;
import cn.kuzuanpa.organapi.client.screen.OrganOverviewScreen;
import cn.kuzuanpa.organapi.client.screen.OrganScreen;
import cn.kuzuanpa.organapi.common.registry.OrganMenus;
import net.minecraft.client.gui.screens.MenuScreens;

public final class ClientSetup {
    private ClientSetup() {
    }

    public static void registerScreens() {
        MenuScreens.register(OrganMenus.ORGAN_MENU.get(), OrganScreen::new);
        MenuScreens.register(OrganMenus.BODY_PART_SELECTION_MENU.get(), BodyPartSelectionScreen::new);
        MenuScreens.register(OrganMenus.ORGAN_OVERVIEW_MENU.get(), OrganOverviewScreen::new);
    }
}
