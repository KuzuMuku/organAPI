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
import cn.kuzuanpa.organapi.core.model.OrganId;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;

/** Native-Gson parser: body-part JSON object → {@link BodyPartDefinition}. */
public final class BodyPartDefinitionParser {
    private BodyPartDefinitionParser() {
    }

    /**
     * @param definitionId the resolved id of this body part
     * @param json         the file body
     * @param sortOrderFallback the sort order used when the file omits one
     *                          (the loader passes the running definition count,
     *                          preserving the original behavior)
     */
    public static BodyPartDefinition parse(OrganId definitionId, JsonObject json, int sortOrderFallback) {
        String translationKey = JsonHelper.getString(json, "translation_key",
                "body_part." + definitionId.namespace() + "." + definitionId.path());
        int defaultCapacity = JsonHelper.getInt(json, "default_capacity", 3);
        Integer maxCapacity = json.has("max_capacity") ? JsonHelper.getInt(json, "max_capacity", 0) : null;
        int sortOrder = JsonHelper.getInt(json, "sort_order", sortOrderFallback);
        float visualWidthRatio = JsonHelper.getFloat(json, "visual_width_ratio", 1.0F);
        float visualHeightRatio = JsonHelper.getFloat(json, "visual_height_ratio", 1.0F);
        List<OrganId> acceptedTags = readAcceptedTags(json.has("accepted_tags")
                ? JsonHelper.getArray(json, "accepted_tags")
                : new JsonArray());
        return new BodyPartDefinition(definitionId, translationKey, defaultCapacity, maxCapacity, sortOrder,
                acceptedTags, visualWidthRatio, visualHeightRatio, readOverviewArea(json));
    }

    static List<OrganId> readAcceptedTags(JsonArray array) {
        if (array.isEmpty()) {
            return List.of(BodyPartDefinition.ORGAN_TAG);
        }
        return array.asList().stream()
                .map(JsonElement::getAsString)
                .map(OrganId::parse)
                .toList();
    }

    static BodyPartDefinition.OverviewArea readOverviewArea(JsonObject json) {
        if (!json.has("overview_area")) {
            return null;
        }
        JsonObject area = JsonHelper.getObject(json, "overview_area");
        return new BodyPartDefinition.OverviewArea(
                JsonHelper.getInt(area, "x", 0),
                JsonHelper.getInt(area, "y", 0),
                JsonHelper.getInt(area, "width", 1),
                JsonHelper.getInt(area, "height", 1));
    }
}
