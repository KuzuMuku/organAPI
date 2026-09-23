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

package cn.kuzuanpa.organapi.core.parse;

import cn.kuzuanpa.organapi.core.model.BodyPartDefinition;
import cn.kuzuanpa.organapi.core.model.BodyPlanDefinition;
import cn.kuzuanpa.organapi.core.model.OrganId;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Native-Gson parser: body-plan JSON object → {@link BodyPlanDefinition}. */
public final class BodyPlanDefinitionParser {
    private BodyPlanDefinitionParser() {
    }

    public static BodyPlanDefinition parse(OrganId planId, JsonObject json) {
        List<OrganId> entityTypes = readIds(json.has("entity_types")
                ? JsonHelper.getArray(json, "entity_types")
                : new JsonArray());
        Map<OrganId, BodyPlanDefinition.PartDefinition> parts = readParts(json.has("parts")
                ? JsonHelper.getObject(json, "parts")
                : new JsonObject());
        return new BodyPlanDefinition(planId, entityTypes, parts);
    }

    private static Map<OrganId, BodyPlanDefinition.PartDefinition> readParts(JsonObject json) {
        Map<OrganId, BodyPlanDefinition.PartDefinition> parts = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (!entry.getValue().isJsonObject()) {
                continue;
            }
            JsonObject partJson = entry.getValue().getAsJsonObject();
            OrganId bodyPartId = OrganId.parse(entry.getKey());
            boolean enabled = JsonHelper.getBoolean(partJson, "enabled", true);
            String translationKey = partJson.has("translation_key") ? JsonHelper.getRequiredString(partJson, "translation_key") : null;
            Integer capacity = partJson.has("capacity")
                    ? JsonHelper.getInt(partJson, "capacity", 0)
                    : (partJson.has("default_capacity") ? JsonHelper.getInt(partJson, "default_capacity", 0) : 0);
            Integer maxCapacity = partJson.has("max_capacity") ? JsonHelper.getInt(partJson, "max_capacity", 0) : null;
            Integer sortOrder = partJson.has("sort_order") ? JsonHelper.getInt(partJson, "sort_order", 0) : null;
            List<OrganId> acceptedTags = partJson.has("accepted_tags")
                    ? BodyPartDefinitionParser.readAcceptedTags(JsonHelper.getArray(partJson, "accepted_tags"))
                    : null;
            Float visualWidthRatio = partJson.has("visual_width_ratio") ? JsonHelper.getFloat(partJson, "visual_width_ratio", 0F) : null;
            Float visualHeightRatio = partJson.has("visual_height_ratio") ? JsonHelper.getFloat(partJson, "visual_height_ratio", 0F) : null;
            BodyPartDefinition.OverviewArea overviewArea = partJson.has("overview_area")
                    ? BodyPartDefinitionParser.readOverviewArea(partJson)
                    : null;
            parts.put(bodyPartId, new BodyPlanDefinition.PartDefinition(enabled, translationKey, capacity, maxCapacity, sortOrder,
                    acceptedTags, visualWidthRatio, visualHeightRatio, overviewArea));
        }
        return parts;
    }

    private static List<OrganId> readIds(JsonArray array) {
        return array.asList().stream()
                .map(JsonElement::getAsString)
                .map(OrganId::parse)
                .toList();
    }
}
