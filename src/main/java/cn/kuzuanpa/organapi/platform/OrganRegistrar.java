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
 * Content registration seam (items, blocks, menus, creative tab). Forge wires
 * its {@code DeferredRegister}s onto the mod event bus here; the NeoForge port
 * will attach its own deferred registers / {@code RegisterEvent}. The concrete
 * registry holders stay loader-specific because their element types differ per
 * version.
 */
public interface OrganRegistrar {
    /** Hooks all content registries into the loader's registration lifecycle. */
    void register();
}
