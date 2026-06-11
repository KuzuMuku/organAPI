package cn.kuzuanpa.organapi.client.screen;

import cn.kuzuanpa.organapi.common.menu.OrganMenu;
import cn.kuzuanpa.organapi.common.network.CycleBodyPartC2SPacket;
import cn.kuzuanpa.organapi.common.network.OrganApiNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import static cn.kuzuanpa.organapi.client.screen.OrganScreenLayout.*;

public class OrganScreen extends AbstractOrganApiScreen<OrganMenu> {
    public OrganScreen(OrganMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = OrganScreenLayout.GUI_WIDTH;
        this.imageHeight = OrganScreenLayout.GUI_HEIGHT;
        this.titleLabelX = 12;
        this.titleLabelY = 8;
        this.inventoryLabelX = 25;
        this.inventoryLabelY = menu.getPlayerInventoryStartY() - 12;
    }

    @Override
    protected void init() {
        super.init();
        positionSlots();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        positionSlots();
        inventoryLabelY = menu.getPlayerInventoryStartY() - 12;
    }

    private void positionSlots() {
        OrganScreenLayout.GridDimensions grid = OrganScreenLayout.organGrid(menu.getSelectedBodyPartId(), menu.getVisibleOrganSlotCount());
        for (int index = 0; index < OrganMenu.MAX_ORGAN_SLOTS; index++) {
            Slot slot = menu.slots.get(index);
            if (index < menu.getVisibleOrganSlotCount()) {
                SlotPositioning.setPosition(slot, OrganScreenLayout.slotX(grid, index), OrganScreenLayout.slotY(grid, index));
            } else {
                SlotPositioning.setPosition(slot, -1000, -1000);
            }
        }
        int startY = menu.getPlayerInventoryStartY();
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                Slot slot = menu.slots.get(OrganMenu.MAX_ORGAN_SLOTS + column + row * 9);
                SlotPositioning.setPosition(slot, 25 + column * 18, startY + row * 18);
            }
        }
        for (int hotbar = 0; hotbar < 9; hotbar++) {
            Slot slot = menu.slots.get(OrganMenu.MAX_ORGAN_SLOTS + 27 + hotbar);
            SlotPositioning.setPosition(slot, 25 + hotbar * 18, startY + 58);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        OrganScreenLayout.GridDimensions grid = OrganScreenLayout.organGrid(menu.getSelectedBodyPartId(), menu.getVisibleOrganSlotCount());
        int totalWidth = grid.columns() * OrganScreenLayout.SLOT_SIZE + 6;
        int totalHeight = grid.rows() * OrganScreenLayout.SLOT_SIZE + 6;
        int panelX = leftPos + (imageWidth - totalWidth) / 2 - 1;
        int panelY = topPos + (PLAYER_INV_START_Y - totalHeight)/2 - 1;
        renderPanel(graphics, 8, 4, imageWidth - 16, menu.getPlayerInventoryStartY() - 24);
        graphics.fill(panelX - 1, panelY, panelX + totalWidth, panelY + totalHeight, STRONG_BORDER_COLOR);

        for (int index = 0; index < menu.getVisibleOrganSlotCount(); index++) {
            int x = leftPos + OrganScreenLayout.slotX(grid, index);
            int y = topPos + OrganScreenLayout.slotY(grid, index);
            renderAbsoluteSlotBackground(graphics, x, y);
        }

        int inventoryStartY = menu.getPlayerInventoryStartY();
        renderWeakPanel(graphics, 21, inventoryStartY - 4, 9 * 18 + 6, 4 * 18 + 10);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, LABEL_COLOR, false);
        var selected = menu.getSelectedBodyPartId();
        graphics.drawCenteredString(font,
                bodyPartName(selected),
                imageWidth / 2,
                12,
                TITLE_COLOR);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, LABEL_COLOR, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
