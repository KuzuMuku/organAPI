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

package cn.kuzuanpa.organapi.platform;

/**
 * Custom-pack loading seam (the step-1 {@code organs/} run-directory packs).
 * Forge registers a {@code RepositorySource} through {@code AddPackFindersEvent};
 * the NeoForge port uses the equivalent event with its changed pack-construction
 * API. Also owns first-launch extraction of the bundled sample pack.
 */
public interface OrganPackSource {
    /** Extracts bundled default packs to the run directory if absent. */
    void extractDefaults();

    /** Registers the run-directory pack repository source with the loader. */
    void register();
}
