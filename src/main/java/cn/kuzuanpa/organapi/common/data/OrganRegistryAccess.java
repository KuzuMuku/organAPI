package cn.kuzuanpa.organapi.common.data;

import cn.kuzuanpa.organapi.api.body.BodyPartDefinition;
import cn.kuzuanpa.organapi.api.organ.OrganDefinition;
import cn.kuzuanpa.organapi.common.item.OrganItem;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class OrganRegistryAccess {
    private static final Map<ResourceLocation, BodyPartDefinition> BODY_PARTS = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, OrganDefinition> ORGANS = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, OrganDefinition> ORGANS_BY_ITEM = new ConcurrentHashMap<>();

    private OrganRegistryAccess() {
    }

    public static Collection<BodyPartDefinition> getBodyParts() {
        return BODY_PARTS.values().stream().sorted(Comparator.comparingInt(BodyPartDefinition::sortOrder)).toList();
    }

    public static List<ResourceLocation> getOrderedBodyPartIds() {
        return getBodyParts().stream().map(BodyPartDefinition::id).toList();
    }

    public static Optional<BodyPartDefinition> getBodyPart(ResourceLocation id) {
        return Optional.ofNullable(BODY_PARTS.get(id));
    }

    public static Collection<OrganDefinition> getOrgans() {
        return ORGANS.values();
    }

    public static Optional<OrganDefinition> getOrgan(ResourceLocation id) {
        return Optional.ofNullable(ORGANS.get(id));
    }

    public static Optional<OrganDefinition> getOrgan(ItemStack stack) {
        if (stack.getItem() instanceof OrganItem organItem) {
            return getOrgan(organItem.getDefinitionId(stack));
        }
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return itemId == null ? Optional.empty() : Optional.ofNullable(ORGANS_BY_ITEM.get(itemId));
    }

    public static void replaceBodyParts(Map<ResourceLocation, BodyPartDefinition> replacements) {
        BODY_PARTS.clear();
        BODY_PARTS.putAll(replacements);
    }

    public static void replaceOrgans(Map<ResourceLocation, OrganDefinition> replacements, Map<ResourceLocation, OrganDefinition> itemReplacements) {
        ORGANS.clear();
        ORGANS.putAll(replacements);
        ORGANS_BY_ITEM.clear();
        ORGANS_BY_ITEM.putAll(itemReplacements);
    }
}
