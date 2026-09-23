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

package cn.kuzuanpa.organapi;

import cn.kuzuanpa.organapi.common.data.pack.OrganPackTemplateExtractor;

/**
 * Loader-neutral mod bootstrap. The per-loader entry classes
 * ({@code OrganApiForge}, {@code OrganApiNeoForge}) install the platform
 * implementations into {@link cn.kuzuanpa.organapi.platform.Platform} and wire the
 * loader's registration/networking/reload/pack events, then call {@link #init()}
 * for the shared one-time setup that touches no loader API directly.
 */
public final class OrganApiMod {
    public static final String MOD_ID = "organapi";

    private OrganApiMod() {
    }

    /** Shared one-time initialization common to every loader. */
    public static void init() {
        OrganPackTemplateExtractor.extractDefaultsIfAbsent();
    }
}
