package cn.kuzuanpa.organapi.common.data;

import cn.kuzuanpa.organapi.OrganApiMod;
import cn.kuzuanpa.organapi.api.body.BodyPartDefinition;
import cn.kuzuanpa.organapi.api.body.BodyPartIds;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;

public class BodyPartDefinitionLoader extends SimplePreparableReloadListener<Map<ResourceLocation, BodyPartDefinition>> {
    public static final BodyPartDefinitionLoader INSTANCE = new BodyPartDefinitionLoader();
    private static final Gson GSON = new GsonBuilder().create();
    private static final String DIRECTORY = "organapi/body_parts";

    private BodyPartDefinitionLoader() {
    }

    @Override
    protected Map<ResourceLocation, BodyPartDefinition> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, BodyPartDefinition> definitions = new LinkedHashMap<>();
        bootstrap(definitions);
        Map<ResourceLocation, Resource> resources = resourceManager.listResources(DIRECTORY, path -> path.getPath().endsWith(".json"));
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement element = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                if (element == null || !element.isJsonObject()) {
                    continue;
                }
                ResourceLocation definitionId = toDefinitionId(entry.getKey(), DIRECTORY);
                String translationKey = GsonHelper.getAsString(element.getAsJsonObject(), "translation_key",
                        "body_part." + definitionId.getNamespace() + "." + definitionId.getPath());
                int defaultCapacity = GsonHelper.getAsInt(element.getAsJsonObject(), "default_capacity", 3);
                int sortOrder = GsonHelper.getAsInt(element.getAsJsonObject(), "sort_order", definitions.size());
                float visualWidthRatio = GsonHelper.getAsFloat(element.getAsJsonObject(), "visual_width_ratio", 1.0F);
                float visualHeightRatio = GsonHelper.getAsFloat(element.getAsJsonObject(), "visual_height_ratio", 1.0F);
                List<TagKey<Item>> acceptedTags = readAcceptedTags(element.getAsJsonObject().has("accepted_tags")
                        ? GsonHelper.getAsJsonArray(element.getAsJsonObject(), "accepted_tags")
                        : new JsonArray());
                definitions.put(definitionId, new BodyPartDefinition(definitionId, translationKey, defaultCapacity, sortOrder,
                        acceptedTags, visualWidthRatio, visualHeightRatio));
            } catch (IOException exception) {
                throw new IllegalStateException("Failed to load body part definition " + entry.getKey(), exception);
            }
        }
        return definitions;
    }

    @Override
    protected void apply(Map<ResourceLocation, BodyPartDefinition> definitions, ResourceManager resourceManager, ProfilerFiller profiler) {
        OrganRegistryAccess.replaceBodyParts(definitions);
    }

    private void bootstrap(Map<ResourceLocation, BodyPartDefinition> definitions) {
        definitions.put(BodyPartIds.HEAD, new BodyPartDefinition(BodyPartIds.HEAD, "body_part.organapi.head", 2, 0,
                List.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "organs"))), 0.7F, 0.7F));
        definitions.put(BodyPartIds.CHEST, new BodyPartDefinition(BodyPartIds.CHEST, "body_part.organapi.chest", 4, 1,
                List.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "organs"))), 1.3F, 1.2F));
        definitions.put(BodyPartIds.ABDOMEN, new BodyPartDefinition(BodyPartIds.ABDOMEN, "body_part.organapi.abdomen", 3, 2,
                List.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "organs"))), 1.0F, 0.9F));
        definitions.put(BodyPartIds.LEFT_ARM, new BodyPartDefinition(BodyPartIds.LEFT_ARM, "body_part.organapi.left_arm", 2, 3,
                List.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "organs"))), 0.5F, 1.0F));
        definitions.put(BodyPartIds.RIGHT_ARM, new BodyPartDefinition(BodyPartIds.RIGHT_ARM, "body_part.organapi.right_arm", 2, 4,
                List.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "organs"))), 0.5F, 1.0F));
        definitions.put(BodyPartIds.LEFT_LEG, new BodyPartDefinition(BodyPartIds.LEFT_LEG, "body_part.organapi.left_leg", 3, 5,
                List.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "organs"))), 0.45F, 1.3F));
        definitions.put(BodyPartIds.RIGHT_LEG, new BodyPartDefinition(BodyPartIds.RIGHT_LEG, "body_part.organapi.right_leg", 3, 6,
                List.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "organs"))), 0.45F, 1.3F));
    }

    private static ResourceLocation toDefinitionId(ResourceLocation fileId, String directory) {
        String path = fileId.getPath();
        String prefix = directory + "/";
        if (path.startsWith(prefix)) {
            path = path.substring(prefix.length());
        }
        if (path.endsWith(".json")) {
            path = path.substring(0, path.length() - 5);
        }
        return ResourceLocation.fromNamespaceAndPath(fileId.getNamespace(), path);
    }

    private static List<TagKey<Item>> readAcceptedTags(JsonArray array) {
        if (array.isEmpty()) {
            return List.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "organs")));
        }
        return array.asList().stream()
                .map(JsonElement::getAsString)
                .map(ResourceLocation::parse)
                .map(ItemTags::create)
                .toList();
    }
}
