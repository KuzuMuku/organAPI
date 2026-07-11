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

import cn.kuzuanpa.organapi.api.organ.OrganDefinition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class OrganDefinitionLoader extends SimplePreparableReloadListener<Map<ResourceLocation, OrganDefinition>> {
    public static final OrganDefinitionLoader INSTANCE = new OrganDefinitionLoader();
    private static final Gson GSON = new GsonBuilder().create();
    private static final String DIRECTORY = "organapi/organs";
    private static final Logger LOGGER = LogUtils.getLogger();

    private OrganDefinitionLoader() {
    }

    @Override
    protected @NotNull Map<ResourceLocation, OrganDefinition> prepare(ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        Map<ResourceLocation, OrganDefinition> definitions = new LinkedHashMap<>();
        Map<ResourceLocation, Resource> resources = resourceManager.listResources(DIRECTORY, path -> path.getPath().endsWith(".json"));
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement element = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                if (!element.isJsonObject()) {
                    continue;
                }
                ResourceLocation definitionId = toDefinitionId(entry.getKey(), DIRECTORY);
                ResourceLocation itemId = ResourceLocation.parse(GsonHelper.getAsString(element.getAsJsonObject(), "item"));
                Set<ResourceLocation> validParts = readResourceLocations(element.getAsJsonObject().has("valid_parts")
                        ? GsonHelper.getAsJsonArray(element.getAsJsonObject(), "valid_parts")
                        : new JsonArray());
                int size = GsonHelper.getAsInt(element.getAsJsonObject(), "size", 1);
                List<String> tooltips = readStrings(element.getAsJsonObject().has("tooltips")
                        ? GsonHelper.getAsJsonArray(element.getAsJsonObject(), "tooltips")
                        : new JsonArray());
                List<String> tags = readStrings(element.getAsJsonObject().has("tags")
                        ? GsonHelper.getAsJsonArray(element.getAsJsonObject(), "tags")
                        : new JsonArray());
                definitions.put(definitionId, new OrganDefinition(definitionId, itemId, validParts, size, tooltips, tags));
            } catch (IOException exception) {
                throw new IllegalStateException("Failed to load organ definition " + entry.getKey(), exception);
            }
        }
        return definitions;
    }

    @Override
    protected void apply(Map<ResourceLocation, OrganDefinition> definitions, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        Map<ResourceLocation, OrganDefinition> byItem = new LinkedHashMap<>();
        Map<ResourceLocation, List<ResourceLocation>> conflicts = new LinkedHashMap<>();
        for (OrganDefinition definition : definitions.values()) {
            OrganDefinition previous = byItem.putIfAbsent(definition.itemId(), definition);
            if (previous == null) {
                continue;
            }
            conflicts.computeIfAbsent(definition.itemId(), key -> {
                List<ResourceLocation> ids = new ArrayList<>();
                ids.add(previous.id());
                return ids;
            }).add(definition.id());
        }
        conflicts.forEach((itemId, ids) -> {
            byItem.remove(itemId);
            LOGGER.warn("Ignoring plain-item organ fallback for {} because multiple organ definitions reference it: {}", itemId, ids);
        });
        OrganRegistryAccess.replaceOrgans(definitions, byItem);
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

    private static Set<ResourceLocation> readResourceLocations(JsonArray array) {
        return array.asList().stream()
                .map(JsonElement::getAsString)
                .map(ResourceLocation::parse)
                .collect(Collectors.toSet());
    }

    private static List<String> readStrings(JsonArray array) {
        return array.asList().stream().map(JsonElement::getAsString).toList();
    }
}
