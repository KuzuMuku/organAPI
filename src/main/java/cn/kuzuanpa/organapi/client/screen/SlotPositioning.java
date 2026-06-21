package cn.kuzuanpa.organapi.client.screen;

import net.minecraft.world.inventory.Slot;

public final class SlotPositioning {
    private SlotPositioning() {
    }

    //This method used AccessTransformer to move slot
    public static void setPosition(Slot slot, int x, int y) {
        slot.x = x;
        slot.y = y;
    }
}
