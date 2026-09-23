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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Tiny native-Gson helper used by the core parsers. Intentionally avoids
 * Minecraft's {@code GsonHelper} (a loader-side utility); the core module must
 * not depend on any Minecraft type.
 */
public final class JsonHelper {
    private JsonHelper() {
    }

    public static String getString(JsonObject json, String key, String fallback) {
        return json.has(key) ? json.get(key).getAsString() : fallback;
    }

    public static String getRequiredString(JsonObject json, String key) {
        JsonElement element = json.get(key);
        if (element == null) {
            throw new IllegalStateException("Missing required JSON field '" + key + "'");
        }
        return element.getAsString();
    }

    public static boolean getBoolean(JsonObject json, String key, boolean fallback) {
        return json.has(key) ? json.get(key).getAsBoolean() : fallback;
    }

    public static int getInt(JsonObject json, String key, int fallback) {
        return json.has(key) ? json.get(key).getAsInt() : fallback;
    }

    public static float getFloat(JsonObject json, String key, float fallback) {
        return json.has(key) ? json.get(key).getAsFloat() : fallback;
    }

    public static JsonObject getObject(JsonObject json, String key) {
        return json.getAsJsonObject(key);
    }

    public static JsonArray getArray(JsonObject json, String key) {
        return json.getAsJsonArray(key);
    }
}
