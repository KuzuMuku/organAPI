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

import cn.kuzuanpa.organapi.core.model.BodyPartIds;
import cn.kuzuanpa.organapi.core.model.BodyPlanDefinition;
import cn.kuzuanpa.organapi.core.model.OrganId;
import cn.kuzuanpa.organapi.core.parse.BodyPlanDefinitionParser;
import cn.kuzuanpa.organapi.core.parse.DefinitionIds;
import cn.kuzuanpa.organapi.platform.OrganIds;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

/**
 * Platform reload listener: reads body-plan JSON from the MC
 * {@link ResourceManager} and delegates parsing to the core
 * {@link BodyPlanDefinitionParser}.
 */
public class BodyPlanDefinitionLoader extends SimplePreparableReloadListener<Map<OrganId, BodyPlanDefinition>> {
    public static final BodyPlanDefinitionLoader INSTANCE = new BodyPlanDefinitionLoader();
    private static final Gson GSON = new GsonBuilder().create();
    private static final String DIRECTORY = "organapi/body_plans";

    private BodyPlanDefinitionLoader() {
    }

    @Override
    protected @NotNull Map<OrganId, BodyPlanDefinition> prepare(ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        Map<OrganId, BodyPlanDefinition> definitions = new LinkedHashMap<>();
        bootstrap(definitions);
        Map<ResourceLocation, Resource> resources = resourceManager.listResources(DIRECTORY, path -> path.getPath().endsWith(".json"));
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement element = JsonParser.parseReader(reader);
                if (!element.isJsonObject()) continue;

                OrganId planId = DefinitionIds.toDefinitionId(OrganIds.toOrganId(entry.getKey()), DIRECTORY);
                definitions.put(planId, BodyPlanDefinitionParser.parse(planId, element.getAsJsonObject()));
            } catch (IOException exception) {
                throw new IllegalStateException("Failed to load body plan definition " + entry.getKey(), exception);
            }
        }
        return definitions;
    }

    @Override
    protected void apply(@NotNull Map<OrganId, BodyPlanDefinition> definitions, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        BodyPlanRegistryAccess.replaceBodyPlans(definitions);
    }

    private void bootstrap(Map<OrganId, BodyPlanDefinition> definitions) {
        Map<OrganId, BodyPlanDefinition.PartDefinition> parts = new LinkedHashMap<>();
        parts.put(BodyPartIds.HEAD, BodyPlanDefinition.PartDefinition.createEnabled());
        parts.put(BodyPartIds.CHEST, BodyPlanDefinition.PartDefinition.createEnabled());
        parts.put(BodyPartIds.ABDOMEN, BodyPlanDefinition.PartDefinition.createEnabled());
        parts.put(BodyPartIds.LEFT_ARM, BodyPlanDefinition.PartDefinition.createEnabled());
        parts.put(BodyPartIds.RIGHT_ARM, BodyPlanDefinition.PartDefinition.createEnabled());
        parts.put(BodyPartIds.LEFT_LEG, BodyPlanDefinition.PartDefinition.createEnabled());
        parts.put(BodyPartIds.RIGHT_LEG, BodyPlanDefinition.PartDefinition.createEnabled());
        definitions.put(BodyPlanRegistryAccess.DEFAULT_PLAN_ID,
                new BodyPlanDefinition(BodyPlanRegistryAccess.DEFAULT_PLAN_ID,
                        List.of(OrganId.of(OrganId.DEFAULT_NAMESPACE, "player")),
                        parts));
    }
}
