package cn.kuzuanpa.organapi.client.screen;

import cn.kuzuanpa.organapi.common.menu.OrganMenu;
import cn.kuzuanpa.organapi.common.network.CycleBodyPartC2SPacket;
import cn.kuzuanpa.organapi.common.network.OrganApiNetwork;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class OrganScreen extends AbstractContainerScreen<OrganMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("organapi", "textures/gui/organ_screen.png");

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
        addRenderableWidget(Button.builder(Component.literal("<"), button ->
                        OrganApiNetwork.channel().sendToServer(new CycleBodyPartC2SPacket(-1)))
                .bounds(leftPos + 10, topPos + 24, 20, 20)
                .build());
        addRenderableWidget(Button.builder(Component.literal(">"), button ->
                        OrganApiNetwork.channel().sendToServer(new CycleBodyPartC2SPacket(1)))
                .bounds(leftPos + imageWidth - 30, topPos + 24, 20, 20)
                .build());
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
        RenderSystem.setShaderTexture(0, TEXTURE);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        OrganScreenLayout.GridDimensions grid = OrganScreenLayout.organGrid(menu.getSelectedBodyPartId(), menu.getVisibleOrganSlotCount());
        int totalWidth = grid.columns() * OrganScreenLayout.SLOT_SIZE + 6;
        int totalHeight = grid.rows() * OrganScreenLayout.SLOT_SIZE + 6;
        int panelX = leftPos + (imageWidth - totalWidth) / 2 - 3;
        int panelY = topPos + OrganScreenLayout.gridStartY() - 6;
        graphics.fill(panelX, panelY, panelX + totalWidth, panelY + totalHeight, 0x55263A59);

        for (int index = 0; index < menu.getVisibleOrganSlotCount(); index++) {
            int x = leftPos + OrganScreenLayout.slotX(grid, index);
            int y = topPos + OrganScreenLayout.slotY(grid, index);
            graphics.fill(x - 1, y - 1, x + 17, y + 17, 0xAA173256);
            graphics.fill(x, y, x + 16, y + 16, 0xFF3AA65A);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, 0x404040, false);
        var selected = menu.getSelectedBodyPartId();
        graphics.drawCenteredString(font,
                Component.translatable("body_part." + selected.getNamespace() + "." + selected.getPath()),
                imageWidth / 2,
                12,
                0x2F2A24);
        graphics.drawCenteredString(font,
                Component.translatable("menu.organapi.capacity", menu.getUsedCapacity(), menu.getVisibleOrganSlotCount()),
                imageWidth / 2,
                28,
                0x5A5248);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
