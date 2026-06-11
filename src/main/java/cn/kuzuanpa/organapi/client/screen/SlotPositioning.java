package cn.kuzuanpa.organapi.client.screen;

import java.lang.reflect.Field;
import net.minecraft.world.inventory.Slot;

public final class SlotPositioning {
    private static final Field SLOT_X = findField("x");
    private static final Field SLOT_Y = findField("y");

    private SlotPositioning() {
    }

    public static void setPosition(Slot slot, int x, int y) {
        try {
            SLOT_X.setInt(slot, x);
            SLOT_Y.setInt(slot, y);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Failed to move slot", exception);
        }
    }

    private static Field findField(String name) {
        try {
            Field field = Slot.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException exception) {
            throw new IllegalStateException("Missing Slot field: " + name, exception);
        }
    }
}
