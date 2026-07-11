/*
 * This class was created by <kuzuanpa>. It is distributed as
 * part of the organAPI Mod. Get the Source Code in github:
 * https://github.com/KuzuMuku/organAPI
 *
 * organAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.

 * organAPI is Open Source and distributed under the
 * AGPLv3 License: https://www.gnu.org/licenses/agpl-3.0.txt
 *
 */

package cn.kuzuanpa.organapi.common.data;

import cn.kuzuanpa.organapi.OrganApiMod;
import cn.kuzuanpa.organapi.api.body.BodyPartDefinition;
import cn.kuzuanpa.organapi.api.body.BodyPartIds;
import cn.kuzuanpa.organapi.api.body.BodyPlanDefinition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
import org.jetbrains.annotations.NotNull;

public class BodyPlanDefinitionLoader extends SimplePreparableReloadListener<Map<ResourceLocation, BodyPlanDefinition>> {
    public static final BodyPlanDefinitionLoader INSTANCE = new BodyPlanDefinitionLoader();
    private static final Gson GSON = new GsonBuilder().create();
    private static final String DIRECTORY = "organapi/body_plans";

    private BodyPlanDefinitionLoader() {
    }

    @Override
    protected @NotNull Map<ResourceLocation, BodyPlanDefinition> prepare(ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        Map<ResourceLocation, BodyPlanDefinition> definitions = new LinkedHashMap<>();
        bootstrap(definitions);
        Map<ResourceLocation, Resource> resources = resourceManager.listResources(DIRECTORY, path -> path.getPath().endsWith(".json"));
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement element = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                if (!element.isJsonObject()) continue;

                JsonObject json = element.getAsJsonObject();
                ResourceLocation planId = toDefinitionId(entry.getKey(), DIRECTORY);
                List<ResourceLocation> entityTypes = readResourceLocations(json.has("entity_types")
                        ? GsonHelper.getAsJsonArray(json, "entity_types")
                        : new JsonArray());
                Map<ResourceLocation, BodyPlanDefinition.PartDefinition> parts = readParts(json.has("parts")
                        ? GsonHelper.getAsJsonObject(json, "parts")
                        : new JsonObject());
                definitions.put(planId, new BodyPlanDefinition(planId, entityTypes, parts));
            } catch (IOException exception) {
                throw new IllegalStateException("Failed to load body plan definition " + entry.getKey(), exception);
            }
        }
        return definitions;
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, BodyPlanDefinition> definitions, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        BodyPlanRegistryAccess.replaceBodyPlans(definitions);
    }

    private void bootstrap(Map<ResourceLocation, BodyPlanDefinition> definitions) {
        Map<ResourceLocation, BodyPlanDefinition.PartDefinition> parts = new LinkedHashMap<>();
        parts.put(BodyPartIds.HEAD, BodyPlanDefinition.PartDefinition.createEnabled());
        parts.put(BodyPartIds.CHEST, BodyPlanDefinition.PartDefinition.createEnabled());
        parts.put(BodyPartIds.ABDOMEN, BodyPlanDefinition.PartDefinition.createEnabled());
        parts.put(BodyPartIds.LEFT_ARM, BodyPlanDefinition.PartDefinition.createEnabled());
        parts.put(BodyPartIds.RIGHT_ARM, BodyPlanDefinition.PartDefinition.createEnabled());
        parts.put(BodyPartIds.LEFT_LEG, BodyPlanDefinition.PartDefinition.createEnabled());
        parts.put(BodyPartIds.RIGHT_LEG, BodyPlanDefinition.PartDefinition.createEnabled());
        definitions.put(BodyPlanRegistryAccess.DEFAULT_PLAN_ID,
                new BodyPlanDefinition(BodyPlanRegistryAccess.DEFAULT_PLAN_ID,
                        List.of(ResourceLocation.withDefaultNamespace("player")),
                        parts));
    }

    private static Map<ResourceLocation, BodyPlanDefinition.PartDefinition> readParts(JsonObject json) {
        Map<ResourceLocation, BodyPlanDefinition.PartDefinition> parts = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (!entry.getValue().isJsonObject()) {
                continue;
            }
            JsonObject partJson = entry.getValue().getAsJsonObject();
            ResourceLocation bodyPartId = ResourceLocation.parse(entry.getKey());
            boolean enabled = GsonHelper.getAsBoolean(partJson, "enabled", true);
            String translationKey = partJson.has("translation_key") ? GsonHelper.getAsString(partJson, "translation_key") : null;
            Integer capacity = partJson.has("capacity")
                    ? GsonHelper.getAsInt(partJson, "capacity")
                    : (partJson.has("default_capacity") ? GsonHelper.getAsInt(partJson, "default_capacity") : 0);
            Integer maxCapacity = partJson.has("max_capacity") ? GsonHelper.getAsInt(partJson, "max_capacity") : null;
            Integer sortOrder = partJson.has("sort_order") ? GsonHelper.getAsInt(partJson, "sort_order") : null;
            List<TagKey<Item>> acceptedTags = partJson.has("accepted_tags")
                    ? readAcceptedTags(GsonHelper.getAsJsonArray(partJson, "accepted_tags"))
                    : null;
            Float visualWidthRatio = partJson.has("visual_width_ratio") ? GsonHelper.getAsFloat(partJson, "visual_width_ratio") : null;
            Float visualHeightRatio = partJson.has("visual_height_ratio") ? GsonHelper.getAsFloat(partJson, "visual_height_ratio") : null;
            BodyPartDefinition.OverviewArea overviewArea = partJson.has("overview_area") ? readOverviewArea(partJson) : null;
            parts.put(bodyPartId, new BodyPlanDefinition.PartDefinition(enabled, translationKey, capacity, maxCapacity, sortOrder,
                    acceptedTags, visualWidthRatio, visualHeightRatio, overviewArea));
        }
        return parts;
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

    private static BodyPartDefinition.OverviewArea readOverviewArea(JsonObject json) {
        JsonObject area = GsonHelper.getAsJsonObject(json, "overview_area");
        return new BodyPartDefinition.OverviewArea(
                GsonHelper.getAsInt(area, "x", 0),
                GsonHelper.getAsInt(area, "y", 0),
                GsonHelper.getAsInt(area, "width", 1),
                GsonHelper.getAsInt(area, "height", 1));
    }

    private static List<ResourceLocation> readResourceLocations(JsonArray array) {
        return array.asList().stream()
                .map(JsonElement::getAsString)
                .map(ResourceLocation::parse)
                .toList();
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
