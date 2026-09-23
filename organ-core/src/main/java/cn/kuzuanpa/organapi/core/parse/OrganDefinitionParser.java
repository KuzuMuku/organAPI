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

import cn.kuzuanpa.organapi.core.model.OrganDefinition;
import cn.kuzuanpa.organapi.core.model.OrganId;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Native-Gson parser: organ JSON object → {@link OrganDefinition}. */
public final class OrganDefinitionParser {
    private OrganDefinitionParser() {
    }

    public static OrganDefinition parse(OrganId definitionId, JsonObject json) {
        OrganId itemId = OrganId.parse(JsonHelper.getRequiredString(json, "item"));
        Set<OrganId> validParts = readIds(json.has("valid_parts")
                ? JsonHelper.getArray(json, "valid_parts")
                : new JsonArray());
        int size = JsonHelper.getInt(json, "size", 1);
        List<String> tooltips = readStrings(json.has("tooltips")
                ? JsonHelper.getArray(json, "tooltips")
                : new JsonArray());
        List<String> tags = readStrings(json.has("tags")
                ? JsonHelper.getArray(json, "tags")
                : new JsonArray());
        return new OrganDefinition(definitionId, itemId, validParts, size, tooltips, tags);
    }

    private static Set<OrganId> readIds(JsonArray array) {
        return array.asList().stream()
                .map(JsonElement::getAsString)
                .map(OrganId::parse)
                .collect(Collectors.toSet());
    }

    private static List<String> readStrings(JsonArray array) {
        return array.asList().stream().map(JsonElement::getAsString).toList();
    }
}
