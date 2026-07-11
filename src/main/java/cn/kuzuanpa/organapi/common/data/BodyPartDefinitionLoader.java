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

public class BodyPartDefinitionLoader extends SimplePreparableReloadListener<Map<ResourceLocation, BodyPartDefinition>> {
    public static final BodyPartDefinitionLoader INSTANCE = new BodyPartDefinitionLoader();
    private static final Gson GSON = new GsonBuilder().create();
    private static final String DIRECTORY = "organapi/body_parts";

    private BodyPartDefinitionLoader() {
    }

    @Override
    protected @NotNull Map<ResourceLocation, BodyPartDefinition> prepare(ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        Map<ResourceLocation, BodyPartDefinition> definitions = new LinkedHashMap<>();
        bootstrap(definitions);
        Map<ResourceLocation, Resource> resources = resourceManager.listResources(DIRECTORY, path -> path.getPath().endsWith(".json"));
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement element = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                if (!element.isJsonObject()) continue;

                JsonObject json = element.getAsJsonObject();
                ResourceLocation definitionId = toDefinitionId(entry.getKey(), DIRECTORY);
                String translationKey = GsonHelper.getAsString(json, "translation_key",
                        "body_part." + definitionId.getNamespace() + "." + definitionId.getPath());
                int defaultCapacity = GsonHelper.getAsInt(json, "default_capacity", 3);
                Integer maxCapacity = json.has("max_capacity") ? GsonHelper.getAsInt(json, "max_capacity") : null;
                int sortOrder = GsonHelper.getAsInt(json, "sort_order", definitions.size());
                float visualWidthRatio = GsonHelper.getAsFloat(json, "visual_width_ratio", 1.0F);
                float visualHeightRatio = GsonHelper.getAsFloat(json, "visual_height_ratio", 1.0F);
                List<TagKey<Item>> acceptedTags = readAcceptedTags(json.has("accepted_tags")
                        ? GsonHelper.getAsJsonArray(json, "accepted_tags")
                        : new JsonArray());
                definitions.put(definitionId, new BodyPartDefinition(definitionId, translationKey, defaultCapacity, maxCapacity, sortOrder,
                        acceptedTags, visualWidthRatio, visualHeightRatio, readOverviewArea(json)));
            } catch (IOException exception) {
                throw new IllegalStateException("Failed to load body part definition " + entry.getKey(), exception);
            }
        }
        return definitions;
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, BodyPartDefinition> definitions, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        OrganRegistryAccess.replaceBodyParts(definitions);
    }

    private void bootstrap(Map<ResourceLocation, BodyPartDefinition> definitions) {
        definitions.put(BodyPartIds.HEAD, new BodyPartDefinition(BodyPartIds.HEAD, "body_part.organapi.head", 2, 36, 0,
                List.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "organs"))), 0.7F, 0.7F,
                new BodyPartDefinition.OverviewArea(36, 0, 28, 28)));
        definitions.put(BodyPartIds.CHEST, new BodyPartDefinition(BodyPartIds.CHEST, "body_part.organapi.chest", 4, 36, 1,
                List.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "organs"))), 1.3F, 1.2F,
                new BodyPartDefinition.OverviewArea(26, 34, 48, 58)));
        definitions.put(BodyPartIds.ABDOMEN, new BodyPartDefinition(BodyPartIds.ABDOMEN, "body_part.organapi.abdomen", 3, 36, 2,
                List.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "organs"))), 1.0F, 0.9F,
                new BodyPartDefinition.OverviewArea(26, 96, 48, 40)));
        definitions.put(BodyPartIds.LEFT_ARM, new BodyPartDefinition(BodyPartIds.LEFT_ARM, "body_part.organapi.left_arm", 2, 36, 3,
                List.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "organs"))), 0.5F, 1.0F,
                new BodyPartDefinition.OverviewArea(0, 34, 22, 80)));
        definitions.put(BodyPartIds.RIGHT_ARM, new BodyPartDefinition(BodyPartIds.RIGHT_ARM, "body_part.organapi.right_arm", 2, 36, 4,
                List.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "organs"))), 0.5F, 1.0F,
                new BodyPartDefinition.OverviewArea(78, 34, 22, 80)));
        definitions.put(BodyPartIds.LEFT_LEG, new BodyPartDefinition(BodyPartIds.LEFT_LEG, "body_part.organapi.left_leg", 3, 36, 5,
                List.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "organs"))), 0.45F, 1.3F,
                new BodyPartDefinition.OverviewArea(25, 140, 22, 78)));
        definitions.put(BodyPartIds.RIGHT_LEG, new BodyPartDefinition(BodyPartIds.RIGHT_LEG, "body_part.organapi.right_leg", 3, 36, 6,
                List.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "organs"))), 0.45F, 1.3F,
                new BodyPartDefinition.OverviewArea(53, 140, 22, 78)));
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
        if (!json.has("overview_area")) {
            return null;
        }
        JsonObject area = GsonHelper.getAsJsonObject(json, "overview_area");
        return new BodyPartDefinition.OverviewArea(
                GsonHelper.getAsInt(area, "x", 0),
                GsonHelper.getAsInt(area, "y", 0),
                GsonHelper.getAsInt(area, "width", 1),
                GsonHelper.getAsInt(area, "height", 1));
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
