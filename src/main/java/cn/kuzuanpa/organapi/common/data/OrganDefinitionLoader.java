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

import cn.kuzuanpa.organapi.core.model.OrganDefinition;
import cn.kuzuanpa.organapi.core.model.OrganId;
import cn.kuzuanpa.organapi.core.parse.DefinitionIds;
import cn.kuzuanpa.organapi.core.parse.OrganDefinitionParser;
import cn.kuzuanpa.organapi.platform.OrganIds;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

/**
 * Platform reload listener: reads organ JSON bytes from the MC
 * {@link ResourceManager}, hands them to the core {@link OrganDefinitionParser},
 * and stores the resulting core models. All JSON-schema logic lives in the core.
 */
public class OrganDefinitionLoader extends SimplePreparableReloadListener<Map<OrganId, OrganDefinition>> {
    public static final OrganDefinitionLoader INSTANCE = new OrganDefinitionLoader();
    private static final Gson GSON = new GsonBuilder().create();
    private static final String DIRECTORY = "organapi/organs";
    private static final Logger LOGGER = LogUtils.getLogger();

    private OrganDefinitionLoader() {
    }

    @Override
    protected @NotNull Map<OrganId, OrganDefinition> prepare(ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        Map<OrganId, OrganDefinition> definitions = new LinkedHashMap<>();
        Map<ResourceLocation, Resource> resources = resourceManager.listResources(DIRECTORY, path -> path.getPath().endsWith(".json"));
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement element = JsonParser.parseReader(reader);
                if (!element.isJsonObject()) {
                    continue;
                }
                OrganId definitionId = DefinitionIds.toDefinitionId(OrganIds.toOrganId(entry.getKey()), DIRECTORY);
                definitions.put(definitionId, OrganDefinitionParser.parse(definitionId, element.getAsJsonObject()));
            } catch (IOException exception) {
                throw new IllegalStateException("Failed to load organ definition " + entry.getKey(), exception);
            }
        }
        return definitions;
    }

    @Override
    protected void apply(Map<OrganId, OrganDefinition> definitions, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        Map<OrganId, OrganDefinition> byItem = new LinkedHashMap<>();
        Map<OrganId, List<OrganId>> conflicts = new LinkedHashMap<>();
        for (OrganDefinition definition : definitions.values()) {
            OrganDefinition previous = byItem.putIfAbsent(definition.itemId(), definition);
            if (previous == null) {
                continue;
            }
            conflicts.computeIfAbsent(definition.itemId(), key -> {
                List<OrganId> ids = new ArrayList<>();
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
}
