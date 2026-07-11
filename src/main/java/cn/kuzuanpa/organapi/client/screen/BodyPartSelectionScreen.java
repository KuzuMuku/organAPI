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
import cn.kuzuanpa.organapi.common.menu.BodyPartSelectionMenu;
import cn.kuzuanpa.organapi.common.network.OpenOrganMenuC2SPacket;
import cn.kuzuanpa.organapi.common.network.OrganApiNetwork;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BodyPartSelectionScreen extends AbstractOrganApiScreen<BodyPartSelectionMenu> {
    private static final int BODY_OFFSET = 4;
    private static final int INFO_PANEL_X = OrganOverviewLayout.EDITOR_PANEL_X;
    private static final int INFO_PANEL_Y = OrganOverviewLayout.EDITOR_PANEL_Y;
    private static final int INFO_PANEL_WIDTH = OrganOverviewLayout.EDITOR_PANEL_WIDTH;
    private static final int INFO_PANEL_HEIGHT = OrganOverviewLayout.EDITOR_PANEL_HEIGHT;

    public BodyPartSelectionScreen(BodyPartSelectionMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = OrganOverviewLayout.GUI_WIDTH;
        this.imageHeight = OrganOverviewLayout.GUI_HEIGHT;
        this.inventoryLabelX = OrganOverviewLayout.HOTBAR_START_X;
        this.inventoryLabelY = OrganOverviewLayout.HOTBAR_Y - 12;
        this.titleLabelX = 10;
        this.titleLabelY = 6;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        renderWeakPanel(graphics,
                OrganOverviewLayout.BODY_AREA_X,
                OrganOverviewLayout.BODY_AREA_Y,
                OrganOverviewLayout.BODY_AREA_WIDTH,
                OrganOverviewLayout.BODY_AREA_HEIGHT);
        renderPanel(graphics, INFO_PANEL_X, INFO_PANEL_Y, INFO_PANEL_WIDTH, INFO_PANEL_HEIGHT);
        renderWeakPanel(graphics,
                OrganOverviewLayout.INVENTORY_START_X - 4,
                OrganOverviewLayout.INVENTORY_START_Y - 4,
                9 * 18 + 6,
                4 * 18 + 10);

        List<OrganOverviewLayout.BodyPartArea> areas = OrganOverviewLayout.bodyPartAreas(menu.getBodyPartIds(), menu.getSelectedBodyPartId(), menu.getTarget());
        for (OrganOverviewLayout.BodyPartArea area : areas) {
            boolean hovered = isMouseOverArea(area, mouseX, mouseY);
            int absoluteX = absoluteAreaX(area);
            int absoluteY = absoluteAreaY(area);
            int backgroundColor = hovered ? BODY_HOVER_BACKGROUND_COLOR : BODY_BACKGROUND_COLOR;
            int borderColor = hovered ? STRONG_BORDER_COLOR : BODY_BORDER_COLOR;
            int slotColor = hovered ? 0xFF444444 : SLOT_BACKGROUND_COLOR;
            graphics.fill(absoluteX, absoluteY, absoluteX + area.width(), absoluteY + area.height(), borderColor);
            graphics.fill(absoluteX + 1, absoluteY + 1, absoluteX + area.width() - 1, absoluteY + area.height() - 1, backgroundColor);

            BodyPartOverview overview = menu.getOverview(area.bodyPartId());
            int capacity = Math.max(1, overview.totalCapacity());
            OrganOverviewLayout.PreviewLayout previewLayout = OrganOverviewLayout.previewLayout(area, area.bodyPartId(), capacity, menu.getTarget());
            for (int slotIndex = 0; slotIndex < capacity; slotIndex++) {
                int slotX = leftPos + BODY_OFFSET + OrganOverviewLayout.BODY_AREA_X + OrganOverviewLayout.previewSlotX(previewLayout, slotIndex);
                int slotY = topPos + BODY_OFFSET + OrganOverviewLayout.BODY_AREA_Y + OrganOverviewLayout.previewSlotY(previewLayout, slotIndex);
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

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, LABEL_COLOR, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, LABEL_COLOR, false);

        OrganOverviewLayout.BodyPartArea hovered = hoveredArea(mouseX, mouseY);
        ResourceLocation bodyPartId = hovered != null ? hovered.bodyPartId() : menu.getSelectedBodyPartId();
        BodyPartOverview overview = menu.getOverview(bodyPartId);
        graphics.drawString(font, bodyPartName(bodyPartId), INFO_PANEL_X + 10, INFO_PANEL_Y + 12, TITLE_COLOR, false);
        graphics.drawString(font,
                Component.translatable("menu.organapi.capacity", overview.usedCapacity(), overview.totalCapacity()),
                INFO_PANEL_X + 10,
                INFO_PANEL_Y + 28,
                SUBTITLE_COLOR,
                false);
        graphics.drawString(font,
                Component.literal("Click a body part to open"),
                INFO_PANEL_X + 10,
                INFO_PANEL_Y + 46,
                MUTED_LABEL_COLOR,
                false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        List<OrganOverviewLayout.BodyPartArea> areas = OrganOverviewLayout.bodyPartAreas(menu.getBodyPartIds(), menu.getSelectedBodyPartId(), menu.getTarget());
        for (OrganOverviewLayout.BodyPartArea area : areas) {
            if (isMouseOverArea(area, mouseX, mouseY)) {
                OrganApiNetwork.channel().sendToServer(new OpenOrganMenuC2SPacket(menu.getTargetEntityId(), area.bodyPartId()));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);

        ItemStack hoveredStack = hoveredPreviewStack(mouseX, mouseY);
        if (!hoveredStack.isEmpty()) {
            graphics.renderTooltip(font, hoveredStack, mouseX, mouseY);
            renderTooltip(graphics, mouseX, mouseY);
            return;
        }
        renderTooltip(graphics, mouseX, mouseY);
    }

    private ItemStack hoveredPreviewStack(int mouseX, int mouseY) {
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
                int slotX = leftPos + BODY_OFFSET + OrganOverviewLayout.BODY_AREA_X + OrganOverviewLayout.previewSlotX(previewLayout, slotIndex);
                int slotY = topPos + BODY_OFFSET + OrganOverviewLayout.BODY_AREA_Y + OrganOverviewLayout.previewSlotY(previewLayout, slotIndex);
                if (mouseX >= slotX && mouseX < slotX + previewLayout.cellSize() && mouseY >= slotY && mouseY < slotY + previewLayout.cellSize()) {
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    private OrganOverviewLayout.BodyPartArea hoveredArea(int mouseX, int mouseY) {
        List<OrganOverviewLayout.BodyPartArea> areas = OrganOverviewLayout.bodyPartAreas(menu.getBodyPartIds(), menu.getSelectedBodyPartId(), menu.getTarget());
        for (OrganOverviewLayout.BodyPartArea area : areas) {
            if (isMouseOverArea(area, mouseX, mouseY)) {
                return area;
            }
        }
        return null;
    }

    private boolean isMouseOverArea(OrganOverviewLayout.BodyPartArea area, double mouseX, double mouseY) {
        int absoluteX = absoluteAreaX(area);
        int absoluteY = absoluteAreaY(area);
        return mouseX >= absoluteX && mouseX < absoluteX + area.width() && mouseY >= absoluteY && mouseY < absoluteY + area.height();
    }

    private int absoluteAreaX(OrganOverviewLayout.BodyPartArea area) {
        return leftPos + BODY_OFFSET + OrganOverviewLayout.BODY_AREA_X + area.x();
    }

    private int absoluteAreaY(OrganOverviewLayout.BodyPartArea area) {
        return topPos + BODY_OFFSET + OrganOverviewLayout.BODY_AREA_Y + area.y();
    }
}
