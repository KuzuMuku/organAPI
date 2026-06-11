package cn.kuzuanpa.organapi.common.body;

import cn.kuzuanpa.organapi.OrganApiMod;
import cn.kuzuanpa.organapi.api.body.BodyPartDefinition;
import cn.kuzuanpa.organapi.api.body.BodyPlanDefinition;
import cn.kuzuanpa.organapi.api.body.ResolvedBodyPlan;
import cn.kuzuanpa.organapi.common.data.BodyPlanRegistryAccess;
import cn.kuzuanpa.organapi.common.data.OrganRegistryAccess;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public final class BodyPlanResolver {
    private static final List<TagKey<Item>> DEFAULT_ACCEPTED_TAGS = List.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "organs")));

    private BodyPlanResolver() {
    }

    public static ResolvedBodyPlan resolve(Entity entity) {
        return resolve(entity.getType());
    }

    public static ResolvedBodyPlan resolve(EntityType<?> entityType) {
        Optional<BodyPlanDefinition> plan = ForgeRegistries.ENTITY_TYPES.getKey(entityType) == null
                ? Optional.empty()
                : BodyPlanRegistryAccess.getBodyPlanForEntityType(ForgeRegistries.ENTITY_TYPES.getKey(entityType));
        if (plan.isPresent()) {
            return resolve(plan.get());
        }
        return fallbackPlan();
    }

    public static List<ResourceLocation> getOrderedBodyPartIds(Entity entity) {
        return resolve(entity).getOrderedBodyPartIds();
    }

    public static List<BodyPartDefinition> getBodyParts(Entity entity) {
        return List.copyOf(resolve(entity).bodyParts());
    }

    public static Optional<BodyPartDefinition> getBodyPart(Entity entity, ResourceLocation bodyPartId) {
        return resolve(entity).getBodyPart(bodyPartId);
    }

    public static ResourceLocation getDefaultBodyPartId(Entity entity, ResourceLocation fallback) {
        return resolve(entity).getDefaultBodyPartId(fallback);
    }

    private static ResolvedBodyPlan resolve(BodyPlanDefinition plan) {
        Map<ResourceLocation, BodyPartDefinition> resolvedParts = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, BodyPlanDefinition.PartDefinition> entry : plan.parts().entrySet()) {
            if (!entry.getValue().enabled()) {
                continue;
            }
            ResourceLocation bodyPartId = entry.getKey();
            BodyPlanDefinition.PartDefinition override = entry.getValue();
            Optional<BodyPartDefinition> template = OrganRegistryAccess.getBodyPart(bodyPartId);
            String translationKey = override.translationKey() != null
                    ? override.translationKey()
                    : template.map(BodyPartDefinition::translationKey).orElse("body_part." + bodyPartId.getNamespace() + "." + bodyPartId.getPath());
            int defaultCapacity = Math.max(0, override.capacity() != null
                    ? override.capacity()
                    : template.map(BodyPartDefinition::defaultCapacity).orElse(0));
            Integer maxCapacity = override.maxCapacity() != null
                    ? Math.max(defaultCapacity, override.maxCapacity())
                    : template.map(BodyPartDefinition::maxCapacity).orElse(null);
            int sortOrder = override.sortOrder() != null
                    ? override.sortOrder()
                    : template.map(BodyPartDefinition::sortOrder).orElse(resolvedParts.size());
            List<TagKey<Item>> acceptedTags = override.acceptedTags() != null
                    ? override.acceptedTags()
                    : template.map(BodyPartDefinition::acceptedTags).orElse(DEFAULT_ACCEPTED_TAGS);
            float visualWidthRatio = override.visualWidthRatio() != null
                    ? override.visualWidthRatio()
                    : template.map(BodyPartDefinition::visualWidthRatio).orElse(1.0F);
            float visualHeightRatio = override.visualHeightRatio() != null
                    ? override.visualHeightRatio()
                    : template.map(BodyPartDefinition::visualHeightRatio).orElse(1.0F);
            BodyPartDefinition.OverviewArea overviewArea = override.overviewArea() != null
                    ? override.overviewArea()
                    : template.map(BodyPartDefinition::overviewArea).orElse(null);
            resolvedParts.put(bodyPartId, new BodyPartDefinition(bodyPartId, translationKey, defaultCapacity, maxCapacity, sortOrder,
                    acceptedTags, visualWidthRatio, visualHeightRatio, overviewArea));
        }
        List<BodyPartDefinition> orderedParts = new ArrayList<>(resolvedParts.values());
        orderedParts.sort(Comparator.comparingInt(BodyPartDefinition::sortOrder));
        Map<ResourceLocation, BodyPartDefinition> orderedMap = new LinkedHashMap<>();
        for (BodyPartDefinition definition : orderedParts) {
            orderedMap.put(definition.id(), definition);
        }
        return new ResolvedBodyPlan(plan.id(), orderedParts, orderedMap);
    }

    private static ResolvedBodyPlan fallbackPlan() {
        return BodyPlanRegistryAccess.getBodyPlan(BodyPlanRegistryAccess.DEFAULT_PLAN_ID)
                .map(BodyPlanResolver::resolve)
                .orElseGet(() -> {
                    List<BodyPartDefinition> bodyParts = OrganRegistryAccess.getBodyParts().stream().toList();
                    Map<ResourceLocation, BodyPartDefinition> byId = new LinkedHashMap<>();
                    for (BodyPartDefinition bodyPart : bodyParts) {
                        byId.put(bodyPart.id(), bodyPart);
                    }
                    return new ResolvedBodyPlan(BodyPlanRegistryAccess.DEFAULT_PLAN_ID, bodyParts, byId);
                });
    }
}
