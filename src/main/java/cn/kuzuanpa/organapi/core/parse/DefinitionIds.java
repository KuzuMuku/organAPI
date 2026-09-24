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

import cn.kuzuanpa.organapi.core.model.OrganId;

/**
 * Pure identifier helpers shared by the loaders. Converting a data-file id
 * ({@code <ns>:<dir>/<name>.json}) to a definition id ({@code <ns>:<name>}) is
 * plain string logic, so it lives in the core module.
 */
public final class DefinitionIds {
    private DefinitionIds() {
    }

    /**
     * Strips the leading directory prefix and trailing {@code .json} from a
     * data-file path, keeping the namespace.
     */
    public static OrganId toDefinitionId(OrganId fileId, String directory) {
        String path = fileId.path();
        String prefix = directory + "/";
        if (path.startsWith(prefix)) {
            path = path.substring(prefix.length());
        }
        if (path.endsWith(".json")) {
            path = path.substring(0, path.length() - 5);
        }
        return OrganId.of(fileId.namespace(), path);
    }
}
