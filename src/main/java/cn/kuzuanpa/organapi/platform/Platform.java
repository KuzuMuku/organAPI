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

import java.util.Objects;

/**
 * Holds the loader-specific implementations of the platform interfaces, injected
 * once by the loader entrypoint ({@code OrganApiMod} on Forge). Common code
 * reaches MC-coupled behavior only through here, keeping the coupling behind the
 * {@code platform-api} boundary.
 */
public final class Platform {
    private static OrganStorage storage;
    private static OrganNetwork network;

    private Platform() {
    }

    public static void install(OrganStorage storageImpl, OrganNetwork networkImpl) {
        storage = Objects.requireNonNull(storageImpl, "storage");
        network = Objects.requireNonNull(networkImpl, "network");
    }

    public static OrganStorage storage() {
        if (storage == null) {
            throw new IllegalStateException("OrganStorage platform implementation not installed");
        }
        return storage;
    }

    public static OrganNetwork network() {
        if (network == null) {
            throw new IllegalStateException("OrganNetwork platform implementation not installed");
        }
        return network;
    }
}
