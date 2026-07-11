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

package cn.kuzuanpa.organapi.client.screen;

import cn.kuzuanpa.organapi.api.query.BodyPartOverview;
import cn.kuzuanpa.organapi.common.menu.OrganMenu;
import cn.kuzuanpa.organapi.common.menu.OrganOverviewMenu;
import cn.kuzuanpa.organapi.common.network.OrganApiNetwork;
import cn.kuzuanpa.organapi.common.network.SelectBodyPartC2SPacket;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class OrganOverviewScreen extends AbstractContainerScreen<OrganOverviewMenu> {

    public OrganOverviewScreen(OrganOverviewMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = OrganOverviewLayout.GUI_WIDTH;
        this.imageHeight = OrganOverviewLayout.GUI_HEIGHT;
        this.titleLabelX = 10;
        this.titleLabelY = 6;
        this.inventoryLabelX = OrganOverviewLayout.HOTBAR_START_X;
        this.inventoryLabelY = OrganOverviewLayout.HOTBAR_Y - 12;
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
    }

    private void positionSlots() {
        OrganOverviewLayout.GridDimensions grid = OrganOverviewLayout.editorGrid(menu.getTarget(), menu.getSelectedBodyPartId(), menu.getVisibleOrganSlotCount());
        for (int index = 0; index < OrganMenu.MAX_ORGAN_SLOTS; index++) {
            Slot slot = menu.slots.get(index);
            if (index < menu.getVisibleOrganSlotCount()) {
                SlotPositioning.setPosition(slot, OrganOverviewLayout.editorSlotX(grid, index), OrganOverviewLayout.editorSlotY(grid, index));
            } else {
                SlotPositioning.setPosition(slot, -1000, -1000);
            }
        }
        int inventoryStart = OrganMenu.MAX_ORGAN_SLOTS;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                Slot slot = menu.slots.get(inventoryStart + column + row * 9);
                SlotPositioning.setPosition(slot, OrganOverviewLayout.INVENTORY_START_X + column * 18, OrganOverviewLayout.INVENTORY_START_Y + row * 18);
            }
        }
        int hotbarStart = inventoryStart + 27;
        for (int hotbar = 0; hotbar < 9; hotbar++) {
            Slot slot = menu.slots.get(hotbarStart + hotbar);
            SlotPositioning.setPosition(slot, OrganOverviewLayout.HOTBAR_START_X + hotbar * 18, OrganOverviewLayout.HOTBAR_Y);
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        renderBodyAreas(graphics, mouseX, mouseY);
        renderEditorGrid(graphics);
        graphics.fill(leftPos + OrganOverviewLayout.INVENTORY_START_X -4, topPos+ OrganOverviewLayout.INVENTORY_START_Y -4, leftPos + OrganOverviewLayout.INVENTORY_START_X + 9*18 +2, topPos + OrganOverviewLayout.INVENTORY_START_Y + 4*18+6 , 0x11FF7777);

    }

    private void renderBodyAreas(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.fill(leftPos + OrganOverviewLayout.BODY_AREA_X, topPos+ OrganOverviewLayout.BODY_AREA_Y , leftPos + OrganOverviewLayout.BODY_AREA_X + OrganOverviewLayout.BODY_AREA_WIDTH, topPos + OrganOverviewLayout.BODY_AREA_Y + OrganOverviewLayout.BODY_AREA_HEIGHT , 0x11FF7777);

        List<OrganOverviewLayout.BodyPartArea> areas = OrganOverviewLayout.bodyPartAreas(menu.getBodyPartIds(), menu.getSelectedBodyPartId(), menu.getTarget());
        for (OrganOverviewLayout.BodyPartArea area : areas) {
            int absoluteX = leftPos+ 4 + OrganOverviewLayout.BODY_AREA_X + area.x();
            int absoluteY = topPos + 4 + OrganOverviewLayout.BODY_AREA_Y + area.y();
            int backgroundColor = area.selected() ? 0xFF928888 : 0xFF151111;
            int borderColor = area.selected() ? 0xFFFF7777 : 0xFF551111;
            int slotColor = area.selected() ? 0xFF444444 : 0xFF222222;
            graphics.fill(absoluteX, absoluteY, absoluteX + area.width(), absoluteY + area.height(), borderColor);
            graphics.fill(absoluteX + 1, absoluteY + 1, absoluteX + area.width() - 1, absoluteY + area.height() - 1, backgroundColor);

            BodyPartOverview overview = menu.getOverview(area.bodyPartId());
            int capacity = Math.max(1, overview.totalCapacity());
            OrganOverviewLayout.PreviewLayout previewLayout = OrganOverviewLayout.previewLayout(area, area.bodyPartId(), capacity, menu.getTarget());
            for (int slotIndex = 0; slotIndex < capacity; slotIndex++) {
                int slotX = leftPos+ 4 + OrganOverviewLayout.BODY_AREA_X + OrganOverviewLayout.previewSlotX(previewLayout, slotIndex);
                int slotY = topPos+ 4 + OrganOverviewLayout.BODY_AREA_Y + OrganOverviewLayout.previewSlotY(previewLayout, slotIndex);
                int cellSize = previewLayout.cellSize();
                graphics.fill(slotX, slotY, slotX + cellSize - 1, slotY + cellSize - 1, slotColor);
                if (slotIndex < overview.organs().size()) {
                    ItemStack stack = overview.organs().get(slotIndex);
                    if (!stack.isEmpty()) {
                        renderPreviewItem(graphics, stack, slotX, slotY, previewLayout);
                    }
                }
            }
        }
    }

    private void renderPreviewItem(GuiGraphics graphics, ItemStack stack, int slotX, int slotY, OrganOverviewLayout.PreviewLayout layout) {
        int iconSize = layout.iconSize();
        int iconX = slotX + (layout.cellSize() - iconSize) / 2;
        int iconY = slotY + (layout.cellSize() - iconSize) / 2;
        if (iconSize >= 16) {
            graphics.renderItem(stack, iconX, iconY);
            return;
        }
        float scale = iconSize / 16.0F;
        graphics.pose().pushPose();
        graphics.pose().translate(iconX, iconY, 0.0F);
        graphics.pose().scale(scale, scale, 1.0F);
        graphics.renderItem(stack, 0, 0);
        graphics.pose().popPose();
    }

    private void renderEditorGrid(GuiGraphics graphics) {
        graphics.fill(leftPos + OrganOverviewLayout.EDITOR_PANEL_X, topPos, leftPos + OrganOverviewLayout.EDITOR_PANEL_X + OrganOverviewLayout.EDITOR_PANEL_WIDTH,  topPos + OrganOverviewLayout.EDITOR_PANEL_Y + OrganOverviewLayout.EDITOR_PANEL_HEIGHT + 4, 0x23FFFFFF);

        OrganOverviewLayout.GridDimensions grid = OrganOverviewLayout.editorGrid(menu.getTarget(), menu.getSelectedBodyPartId(), menu.getVisibleOrganSlotCount());
        int totalWidth = grid.columns() * OrganOverviewLayout.SLOT_SIZE + 5;
        int totalHeight = grid.rows() * OrganOverviewLayout.SLOT_SIZE + 6;
        int panelX = leftPos + OrganOverviewLayout.EDITOR_PANEL_X + (OrganOverviewLayout.EDITOR_PANEL_WIDTH - totalWidth) / 2;
        int panelY = topPos + OrganOverviewLayout.EDITOR_PANEL_Y - 9 + (OrganOverviewLayout.EDITOR_PANEL_HEIGHT - totalHeight) / 2;
        graphics.fill(panelX - 1, panelY , panelX + totalWidth, panelY + totalHeight, 0xFFFF7777);

        for (int index = 0; index < menu.getVisibleOrganSlotCount(); index++) {
            int x = leftPos + OrganOverviewLayout.editorSlotX(grid, index);
            int y = topPos + OrganOverviewLayout.editorSlotY(grid, index);
            graphics.fill(x - 2, y - 2, x + 18, y + 18, 0x99AA9999);
            graphics.fill(x, y, x + 16, y + 16, 0xFF222222);
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        List<OrganOverviewLayout.BodyPartArea> areas = OrganOverviewLayout.bodyPartAreas(menu.getBodyPartIds(), menu.getSelectedBodyPartId(), menu.getTarget());
        for (OrganOverviewLayout.BodyPartArea area : areas) {
            int relativeX = OrganOverviewLayout.BODY_AREA_X + area.x();
            int relativeY = OrganOverviewLayout.BODY_AREA_Y + area.y();
            if (isHovering(relativeX, relativeY, area.width(), area.height(), mouseX, mouseY)) {
                OrganApiNetwork.channel().sendToServer(new SelectBodyPartC2SPacket(area.index()));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);

        List<OrganOverviewLayout.BodyPartArea> areas = OrganOverviewLayout.bodyPartAreas(menu.getBodyPartIds(), menu.getSelectedBodyPartId(), menu.getTarget());
        for (OrganOverviewLayout.BodyPartArea area : areas) {
            BodyPartOverview overview = menu.getOverview(area.bodyPartId());
            int capacity = Math.max(1, overview.totalCapacity());
            OrganOverviewLayout.PreviewLayout previewLayout = OrganOverviewLayout.previewLayout(area, area.bodyPartId(), capacity, menu.getTarget());
            for (int slotIndex = 0; slotIndex < capacity && slotIndex < overview.organs().size(); slotIndex++) {
                ItemStack stack = overview.organs().get(slotIndex);
                if (stack.isEmpty()) {
                    continue;
                }
                int iconX = leftPos + OrganOverviewLayout.BODY_AREA_X + 4 + OrganOverviewLayout.previewSlotX(previewLayout, slotIndex);
                int iconY = topPos + OrganOverviewLayout.BODY_AREA_Y+ 4 + OrganOverviewLayout.previewSlotY(previewLayout, slotIndex);
                if (mouseX >= iconX && mouseX < iconX + previewLayout.cellSize() && mouseY >= iconY && mouseY < iconY + previewLayout.cellSize()) {
                    graphics.renderTooltip(font, stack, mouseX, mouseY);
                    renderTooltip(graphics, mouseX, mouseY);
                    return;
                }
            }
        }
        renderTooltip(graphics, mouseX, mouseY);
    }
}
