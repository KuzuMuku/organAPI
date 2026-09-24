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

package cn.kuzuanpa.organapi.core.model;

import java.util.Objects;

/**
 * Version-agnostic identifier, mirrors Minecraft's {@code ResourceLocation}
 * ({@code namespace:path}) without depending on any Minecraft type. The platform
 * layer converts between {@link OrganId} and the loader-specific resource location
 * at its boundary.
 */
public record OrganId(String namespace, String path) {
    public static final String DEFAULT_NAMESPACE = "minecraft";

    public OrganId {
        Objects.requireNonNull(namespace, "namespace");
        Objects.requireNonNull(path, "path");
    }

    public static OrganId of(String namespace, String path) {
        return new OrganId(namespace, path);
    }

    /** Parses {@code "namespace:path"}; a value without a colon uses the default namespace. */
    public static OrganId parse(String value) {
        Objects.requireNonNull(value, "value");
        int separator = value.indexOf(':');
        if (separator < 0) {
            return new OrganId(DEFAULT_NAMESPACE, value);
        }
        return new OrganId(value.substring(0, separator), value.substring(separator + 1));
    }

    @Override
    public String toString() {
        return namespace + ":" + path;
    }
}
