package cn.kuzuanpa.organapi.client.screen;

import cn.kuzuanpa.organapi.api.body.BodyPartDefinition;
import cn.kuzuanpa.organapi.common.body.BodyPlanResolver;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public final class OrganOverviewLayout {
    public static final int GUI_WIDTH = 320;
    public static final int GUI_HEIGHT = 236;
    public static final int INVENTORY_START_X = 142;
    public static final int INVENTORY_START_Y = 156;
    public static final int HOTBAR_START_X = INVENTORY_START_X;
    public static final int HOTBAR_Y = 216;
    public static final int BODY_AREA_X = 4;
    public static final int BODY_AREA_Y = 4;
    public static final int BODY_AREA_WIDTH = 108;
    public static final int BODY_AREA_HEIGHT = 232;
    public static final int EDITOR_PANEL_X = 128;
    public static final int EDITOR_PANEL_Y = 18;
    public static final int EDITOR_PANEL_WIDTH = 184;
    public static final int EDITOR_PANEL_HEIGHT = 126;
    public static final int SLOT_SIZE = 18;
    public static final int PREVIEW_SLOT_SIZE = 16;
    public static final int MIN_PREVIEW_SLOT_SIZE = 6;

    private OrganOverviewLayout() {
    }

    public static GridDimensions previewGrid(Entity entity, ResourceLocation bodyPartId, int capacity) {
        BodyPartDefinition definition = BodyPlanResolver.getBodyPart(entity, bodyPartId)
                .orElse(BodyPartDefinition.simple(bodyPartId, Math.max(1, capacity), 0));
        return gridFor(capacity, definition.visualWidthRatio(), definition.visualHeightRatio(), 1, capacity);
    }

    public static GridDimensions editorGrid(Entity entity, ResourceLocation bodyPartId, int capacity) {
        BodyPartDefinition definition = BodyPlanResolver.getBodyPart(entity, bodyPartId)
                .orElse(BodyPartDefinition.simple(bodyPartId, Math.max(1, capacity), 0));
        return gridFor(capacity, definition.visualWidthRatio(), definition.visualHeightRatio(), 1, 8);
    }

    private static GridDimensions gridFor(int capacity, float widthRatio, float heightRatio, int minColumns, int maxColumns) {
        int safeCapacity = Math.max(1, capacity);
        float base = (float) Math.sqrt(safeCapacity);
        float widthBias = Math.max(0.4F, widthRatio);
        float heightBias = Math.max(0.4F, heightRatio);
        int columns = Math.max(minColumns, Math.round(base * (float) Math.sqrt(widthBias / heightBias)));
        columns = Math.min(columns, Math.max(minColumns, maxColumns));
        int rows = (int) Math.ceil((double) safeCapacity / columns);
        while (columns > minColumns && (rows - 1) * columns >= safeCapacity) {
            rows--;
        }
        return new GridDimensions(columns, rows);
    }

    public static int editorSlotX(GridDimensions grid, int index) {
        int totalWidth = grid.columns() * SLOT_SIZE;
        int startX = EDITOR_PANEL_X + (EDITOR_PANEL_WIDTH - totalWidth) / 2;
        return startX + (index % grid.columns()) * SLOT_SIZE;
    }

    public static int editorSlotY(GridDimensions grid, int index) {
        int totalHeight = grid.rows() * SLOT_SIZE;
        int startY = -8 + EDITOR_PANEL_Y + (EDITOR_PANEL_HEIGHT - totalHeight) / 2;
        return startY + (index / grid.columns()) * SLOT_SIZE;
    }

    public static int previewSlotX(PreviewLayout layout, int index) {
        return layout.startX() + (index % layout.grid().columns()) * layout.cellSize();
    }

    public static int previewSlotY(PreviewLayout layout, int index) {
        return layout.startY() + (index / layout.grid().columns()) * layout.cellSize();
    }

    public static PreviewLayout previewLayout(BodyPartArea area, ResourceLocation bodyPartId, int capacity, Entity entity) {
        GridDimensions grid = previewGrid(entity, bodyPartId, capacity);
        int horizontalPadding = Math.min(2, Math.max(0, area.width() / 6));
        int verticalPadding = Math.min(2, Math.max(0, area.height() / 6));
        int availableWidth = Math.max(1, area.width() - horizontalPadding * 2);
        int availableHeight = Math.max(1, area.height() - verticalPadding * 2);
        int fitCellSize = Math.min(PREVIEW_SLOT_SIZE, Math.min(availableWidth / grid.columns(), availableHeight / grid.rows()));
        int cellSize = Math.max(MIN_PREVIEW_SLOT_SIZE, fitCellSize);
        int totalWidth = grid.columns() * cellSize;
        int totalHeight = grid.rows() * cellSize;
        int startX = area.x() + Math.max(1, (area.width() - totalWidth) / 2);
        int startY = area.y() + Math.max(1, (area.height() - totalHeight) / 2);
        int iconSize = Math.max(1, Math.min(16, cellSize - 2));
        return new PreviewLayout(grid, cellSize, iconSize, startX, startY);
    }

    public static List<BodyPartArea> bodyPartAreas(List<ResourceLocation> bodyPartIds, ResourceLocation selectedBodyPartId, Entity entity) {
        List<BodyPartArea> areas = new ArrayList<>(bodyPartIds.size());
        for (int index = 0; index < bodyPartIds.size(); index++) {
            ResourceLocation bodyPartId = bodyPartIds.get(index);
            boolean selected = bodyPartId.equals(selectedBodyPartId);
            areas.add(createBodyPartArea(entity, bodyPartId, index, selected));
        }
        return areas;
    }

    private static BodyPartArea createBodyPartArea(Entity entity, ResourceLocation bodyPartId, int index, boolean selected) {
        return BodyPlanResolver.getBodyPart(entity, bodyPartId)
                .map(BodyPartDefinition::overviewArea)
                .map(area -> new BodyPartArea(bodyPartId, index, area.x(), area.y(), area.width(), area.height(), selected))
                .orElseGet(() -> new BodyPartArea(bodyPartId, index, 4, 12 + index * 24, 48, 20, selected));
    }

    public record GridDimensions(int columns, int rows) {
    }

    public record PreviewLayout(GridDimensions grid, int cellSize, int iconSize, int startX, int startY) {
    }

    public record BodyPartArea(ResourceLocation bodyPartId, int index, int x, int y, int width, int height, boolean selected) {
    }
}
