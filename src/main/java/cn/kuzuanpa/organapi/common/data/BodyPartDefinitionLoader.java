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

import cn.kuzuanpa.organapi.core.model.BodyPartDefinition;
import cn.kuzuanpa.organapi.core.model.BodyPartIds;
import cn.kuzuanpa.organapi.core.model.OrganId;
import cn.kuzuanpa.organapi.core.parse.BodyPartDefinitionParser;
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
 * Platform reload listener: reads body-part JSON from the MC
 * {@link ResourceManager} and delegates parsing to the core
 * {@link BodyPartDefinitionParser}.
 */
public class BodyPartDefinitionLoader extends SimplePreparableReloadListener<Map<OrganId, BodyPartDefinition>> {
    public static final BodyPartDefinitionLoader INSTANCE = new BodyPartDefinitionLoader();
    private static final Gson GSON = new GsonBuilder().create();
    private static final String DIRECTORY = "organapi/body_parts";

    private BodyPartDefinitionLoader() {
    }

    @Override
    protected @NotNull Map<OrganId, BodyPartDefinition> prepare(ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        Map<OrganId, BodyPartDefinition> definitions = new LinkedHashMap<>();
        bootstrap(definitions);
        Map<ResourceLocation, Resource> resources = resourceManager.listResources(DIRECTORY, path -> path.getPath().endsWith(".json"));
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement element = JsonParser.parseReader(reader);
                if (!element.isJsonObject()) continue;

                OrganId definitionId = DefinitionIds.toDefinitionId(OrganIds.toOrganId(entry.getKey()), DIRECTORY);
                definitions.put(definitionId,
                        BodyPartDefinitionParser.parse(definitionId, element.getAsJsonObject(), definitions.size()));
            } catch (IOException exception) {
                throw new IllegalStateException("Failed to load body part definition " + entry.getKey(), exception);
            }
        }
        return definitions;
    }

    @Override
    protected void apply(@NotNull Map<OrganId, BodyPartDefinition> definitions, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        OrganRegistryAccess.replaceBodyParts(definitions);
    }

    private void bootstrap(Map<OrganId, BodyPartDefinition> definitions) {
        List<OrganId> organTag = List.of(BodyPartDefinition.ORGAN_TAG);
        definitions.put(BodyPartIds.HEAD, new BodyPartDefinition(BodyPartIds.HEAD, "body_part.organapi.head", 2, 36, 0,
                organTag, 0.7F, 0.7F, new BodyPartDefinition.OverviewArea(36, 0, 28, 28)));
        definitions.put(BodyPartIds.CHEST, new BodyPartDefinition(BodyPartIds.CHEST, "body_part.organapi.chest", 4, 36, 1,
                organTag, 1.3F, 1.2F, new BodyPartDefinition.OverviewArea(26, 34, 48, 58)));
        definitions.put(BodyPartIds.ABDOMEN, new BodyPartDefinition(BodyPartIds.ABDOMEN, "body_part.organapi.abdomen", 3, 36, 2,
                organTag, 1.0F, 0.9F, new BodyPartDefinition.OverviewArea(26, 96, 48, 40)));
        definitions.put(BodyPartIds.LEFT_ARM, new BodyPartDefinition(BodyPartIds.LEFT_ARM, "body_part.organapi.left_arm", 2, 36, 3,
                organTag, 0.5F, 1.0F, new BodyPartDefinition.OverviewArea(0, 34, 22, 80)));
        definitions.put(BodyPartIds.RIGHT_ARM, new BodyPartDefinition(BodyPartIds.RIGHT_ARM, "body_part.organapi.right_arm", 2, 36, 4,
                organTag, 0.5F, 1.0F, new BodyPartDefinition.OverviewArea(78, 34, 22, 80)));
        definitions.put(BodyPartIds.LEFT_LEG, new BodyPartDefinition(BodyPartIds.LEFT_LEG, "body_part.organapi.left_leg", 3, 36, 5,
                organTag, 0.45F, 1.3F, new BodyPartDefinition.OverviewArea(25, 140, 22, 78)));
        definitions.put(BodyPartIds.RIGHT_LEG, new BodyPartDefinition(BodyPartIds.RIGHT_LEG, "body_part.organapi.right_leg", 3, 36, 6,
                organTag, 0.45F, 1.3F, new BodyPartDefinition.OverviewArea(53, 140, 22, 78)));
    }
}
