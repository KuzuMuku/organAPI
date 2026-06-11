package cn.kuzuanpa.organapi.client.screen;

import cn.kuzuanpa.organapi.api.body.BodyPartDefinition;
import cn.kuzuanpa.organapi.common.data.OrganRegistryAccess;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public final class OrganScreenLayout {
    public static final int GUI_WIDTH = 212;
    public static final int GUI_HEIGHT = 236;
    public static final int SLOT_SIZE = 18;
    public static final int ORGAN_SLOT_START_Y = 48;
    public static final int PLAYER_INV_START_Y = 154;

    private OrganScreenLayout() {
    }

    public static GridDimensions organGrid(ResourceLocation bodyPartId, int capacity) {
        BodyPartDefinition definition = OrganRegistryAccess.getBodyPart(bodyPartId)
                .orElse(BodyPartDefinition.simple(bodyPartId, Math.max(1, capacity), 0));
        int safeCapacity = Math.max(1, capacity);
        float base = (float) Math.sqrt(safeCapacity);
        int columns = Math.max(1, Math.round(base * (float) Math.sqrt(definition.visualWidthRatio() / definition.visualHeightRatio())));
        columns = Math.min(columns, 8);
        int rows = (int) Math.ceil((double) safeCapacity / columns);
        return new GridDimensions(columns, rows);
    }

    public static int gridStartX(GridDimensions grid) {
        return (GUI_WIDTH - grid.columns() * SLOT_SIZE) / 2;
    }

    public static int gridStartY() {
        return ORGAN_SLOT_START_Y;
    }

    public static int slotX(GridDimensions grid, int index) {
        return gridStartX(grid) + (index % grid.columns()) * SLOT_SIZE;
    }

    public static int slotY(GridDimensions grid, int index) {
        return gridStartY() + (index / grid.columns()) * SLOT_SIZE;
    }

    public static List<Integer> visibleRows(GridDimensions grid) {
        List<Integer> rows = new ArrayList<>(grid.rows());
        for (int i = 0; i < grid.rows(); i++) {
            rows.add(i);
        }
        return rows;
    }

    public record GridDimensions(int columns, int rows) {
    }
}
