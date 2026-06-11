package cn.kuzuanpa.organapi.common.data;

import cn.kuzuanpa.organapi.api.body.BodyPlanDefinition;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public final class BodyPlanRegistryAccess {
    public static final ResourceLocation DEFAULT_PLAN_ID = ResourceLocation.fromNamespaceAndPath("organapi", "humanoid");

    private static Map<ResourceLocation, BodyPlanDefinition> bodyPlans = Map.of();
    private static Map<ResourceLocation, ResourceLocation> entityTypePlans = Map.of();

    private BodyPlanRegistryAccess() {
    }

    public static Collection<BodyPlanDefinition> getBodyPlans() {
        return bodyPlans.values();
    }

    public static Optional<BodyPlanDefinition> getBodyPlan(ResourceLocation id) {
        return Optional.ofNullable(bodyPlans.get(id));
    }

    public static Optional<BodyPlanDefinition> getBodyPlanForEntityType(ResourceLocation entityTypeId) {
        ResourceLocation planId = entityTypePlans.get(entityTypeId);
        if (planId == null) {
            return Optional.empty();
        }
        return getBodyPlan(planId);
    }

    public static void replaceBodyPlans(Map<ResourceLocation, BodyPlanDefinition> replacements) {
        Map<ResourceLocation, BodyPlanDefinition> orderedPlans = new LinkedHashMap<>(replacements);
        Map<ResourceLocation, ResourceLocation> mappings = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, BodyPlanDefinition> entry : orderedPlans.entrySet()) {
            for (ResourceLocation entityTypeId : entry.getValue().entityTypes()) {
                mappings.put(entityTypeId, entry.getKey());
            }
        }
        bodyPlans = Map.copyOf(orderedPlans);
        entityTypePlans = Map.copyOf(mappings);
    }
}
