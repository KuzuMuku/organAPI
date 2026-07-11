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

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractOrganApiScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    protected static final int PANEL_COLOR = 0x23FFFFFF;
    protected static final int WEAK_PANEL_COLOR = 0x11FF7777;
    protected static final int STRONG_BORDER_COLOR = 0xFFFF7777;
    protected static final int SLOT_BORDER_COLOR = 0x99AA9999;
    protected static final int SLOT_BACKGROUND_COLOR = 0xFF222222;
    protected static final int BODY_BACKGROUND_COLOR = 0xFF151111;
    protected static final int BODY_HOVER_BACKGROUND_COLOR = 0xFF928888;
    protected static final int BODY_BORDER_COLOR = 0xFF551111;
    protected static final int LABEL_COLOR = 0xFFE8DADA;
    protected static final int TITLE_COLOR = 0xFFFFCCCC;
    protected static final int SUBTITLE_COLOR = 0xFFE0B0B0;
    protected static final int MUTED_LABEL_COLOR = 0xFFAA9999;

    protected AbstractOrganApiScreen(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    protected void renderPanel(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(leftPos + x, topPos + y, leftPos + x + width, topPos + y + height, PANEL_COLOR);
    }

    protected void renderWeakPanel(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(leftPos + x, topPos + y, leftPos + x + width, topPos + y + height, WEAK_PANEL_COLOR);
    }

    protected void renderAbsoluteSlotBackground(GuiGraphics graphics, int x, int y) {
        graphics.fill(x - 2, y - 2, x + 18, y + 18, SLOT_BORDER_COLOR);
        graphics.fill(x, y, x + 16, y + 16, SLOT_BACKGROUND_COLOR);
    }

    protected void renderPreviewItem(GuiGraphics graphics, ItemStack stack, int slotX, int slotY, OrganOverviewLayout.PreviewLayout layout) {
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

    protected Component bodyPartName(ResourceLocation bodyPartId) {
        return Component.translatable("body_part." + bodyPartId.getNamespace() + "." + bodyPartId.getPath());
    }
}
