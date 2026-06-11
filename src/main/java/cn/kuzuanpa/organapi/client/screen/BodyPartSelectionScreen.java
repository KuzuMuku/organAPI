package cn.kuzuanpa.organapi.client.screen;

import cn.kuzuanpa.organapi.common.menu.BodyPartSelectionMenu;
import cn.kuzuanpa.organapi.common.network.OpenOrganMenuC2SPacket;
import cn.kuzuanpa.organapi.common.network.OrganApiNetwork;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class BodyPartSelectionScreen extends AbstractContainerScreen<BodyPartSelectionMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("organapi", "textures/gui/body_part_selection.png");
    private static final int ENTRY_X = 16;
    private static final int ENTRY_Y = 26;
    private static final int ENTRY_HEIGHT = 16;
    private static final int ENTRY_SPACING = 18;
    private static final int ENTRY_WIDTH = 152;

    public BodyPartSelectionScreen(BodyPartSelectionMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 212;
        this.imageHeight = 222;
        this.inventoryLabelX = 30;
        this.inventoryLabelY = 128;
        this.titleLabelX = 16;
        this.titleLabelY = 8;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        List<ResourceLocation> bodyParts = menu.getBodyPartIds();
        for (int index = 0; index < bodyParts.size(); index++) {
            int x = leftPos + ENTRY_X;
            int y = topPos + ENTRY_Y + index * ENTRY_SPACING;
            boolean hovered = isHovering(ENTRY_X, ENTRY_Y + index * ENTRY_SPACING, ENTRY_WIDTH, ENTRY_HEIGHT, mouseX, mouseY);
            int color = hovered ? 0x44FFFFFF : 0x22000000;
            graphics.fill(x, y, x + ENTRY_WIDTH, y + ENTRY_HEIGHT, color);

            ItemStack preview = menu.getPreviewStack(bodyParts.get(index));
            if (!preview.isEmpty()) {
                graphics.renderItem(preview, x + 3, y);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, 0x404040, false);
        List<ResourceLocation> bodyParts = menu.getBodyPartIds();
        for (int index = 0; index < bodyParts.size(); index++) {
            ResourceLocation bodyPartId = bodyParts.get(index);
            int y = ENTRY_Y + index * ENTRY_SPACING + 4;
            graphics.drawString(font,
                    Component.translatable("body_part." + bodyPartId.getNamespace() + "." + bodyPartId.getPath()),
                    ENTRY_X + 22,
                    y,
                    0x2F2A24,
                    false);
            graphics.drawString(font,
                    Component.translatable("menu.organapi.capacity", menu.getUsedCapacity(bodyPartId), menu.getTotalCapacity(bodyPartId)),
                    ENTRY_X + 82,
                    y,
                    0x5A5248,
                    false);
        }
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        List<ResourceLocation> bodyParts = menu.getBodyPartIds();
        for (int index = 0; index < bodyParts.size(); index++) {
            if (isHovering(ENTRY_X, ENTRY_Y + index * ENTRY_SPACING, ENTRY_WIDTH, ENTRY_HEIGHT, mouseX, mouseY)) {
                OrganApiNetwork.channel().sendToServer(new OpenOrganMenuC2SPacket(bodyParts.get(index)));
                if (minecraft != null && minecraft.player != null) {
                    minecraft.player.closeContainer();
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);

        List<ResourceLocation> bodyParts = menu.getBodyPartIds();
        for (int index = 0; index < bodyParts.size(); index++) {
            if (isHovering(ENTRY_X, ENTRY_Y + index * ENTRY_SPACING, ENTRY_WIDTH, ENTRY_HEIGHT, mouseX, mouseY)) {
                ItemStack preview = menu.getPreviewStack(bodyParts.get(index));
                if (!preview.isEmpty()) {
                    graphics.renderTooltip(font, preview, mouseX, mouseY);
                }
                break;
            }
        }
        renderTooltip(graphics, mouseX, mouseY);
    }
}
