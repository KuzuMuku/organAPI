package cn.kuzuanpa.organapi.api.body;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record BodyPartDefinition(
        ResourceLocation id,
        String translationKey,
        int defaultCapacity,
        Integer maxCapacity,
        int sortOrder,
        List<TagKey<Item>> acceptedTags,
        float visualWidthRatio,
        float visualHeightRatio,
        OverviewArea overviewArea
) {
    public BodyPartDefinition {
        defaultCapacity = Math.max(0, defaultCapacity);
        if (maxCapacity != null) {
            maxCapacity = Math.max(defaultCapacity, maxCapacity);
        }
        acceptedTags = List.copyOf(acceptedTags);
        visualWidthRatio = Math.max(0.1F, visualWidthRatio);
        visualHeightRatio = Math.max(0.1F, visualHeightRatio);
        if (overviewArea != null) {
            overviewArea = new OverviewArea(overviewArea.x(), overviewArea.y(), Math.max(1, overviewArea.width()), Math.max(1, overviewArea.height()));
        }
    }

    public Component getDisplayName() {
        return Component.translatable(translationKey);
    }

    public boolean accepts(ItemStack stack) {
        if (acceptedTags.isEmpty()) {
            return true;
        }
        for (TagKey<Item> tagKey : acceptedTags) {
            if (stack.is(tagKey)) {
                return true;
            }
        }
        return false;
    }

    public static BodyPartDefinition simple(ResourceLocation id, int defaultCapacity, int sortOrder) {
        return new BodyPartDefinition(id, "body_part." + id.getNamespace() + "." + id.getPath(), defaultCapacity, null, sortOrder,
                List.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath("organapi", "organs"))), 1.0F, 1.0F, null);
    }

    public record OverviewArea(int x, int y, int width, int height) {
    }
}
