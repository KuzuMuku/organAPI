package cn.kuzuanpa.organapi.api.query;

import java.util.List;
import net.minecraft.world.item.ItemStack;

public record BodyPartOverview(
        int totalCapacity,
        int usedCapacity,
        List<ItemStack> organs
) {
    public BodyPartOverview {
        organs = List.copyOf(organs);
    }
}
