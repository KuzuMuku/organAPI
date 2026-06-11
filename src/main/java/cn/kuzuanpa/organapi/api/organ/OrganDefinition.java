package cn.kuzuanpa.organapi.api.organ;

import java.util.List;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public record OrganDefinition(
        ResourceLocation id,
        ResourceLocation itemId,
        Set<ResourceLocation> validParts,
        int size,
        List<String> tooltips,
        List<String> tags
) {
    public OrganDefinition {
        validParts = Set.copyOf(validParts);
        tooltips = List.copyOf(tooltips);
        tags = List.copyOf(tags);
    }

    public boolean supports(ResourceLocation partId) {
        return validParts.isEmpty() || validParts.contains(partId);
    }
}
