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
 * once by the loader entrypoint ({@code OrganApiForge} on Forge,
 * {@code OrganApiNeoForge} on NeoForge). Common code reaches MC-coupled behavior
 * only through here, keeping the coupling behind the {@code platform-api}
 * boundary.
 */
public final class Platform {
    private static OrganStorage storage;
    private static OrganNetwork network;
    private static OrganItemCodec itemCodec;
    private static OrganMenuAccess menus;
    private static OrganEvents events;

    private Platform() {
    }

    /**
     * Installs all platform seams. Called once by the loader entrypoint before any
     * common code touches {@link Platform}.
     */
    public static void install(OrganStorage storageImpl,
                               OrganNetwork networkImpl,
                               OrganItemCodec itemCodecImpl,
                               OrganMenuAccess menusImpl,
                               OrganEvents eventsImpl) {
        storage = Objects.requireNonNull(storageImpl, "storage");
        network = Objects.requireNonNull(networkImpl, "network");
        itemCodec = Objects.requireNonNull(itemCodecImpl, "itemCodec");
        menus = Objects.requireNonNull(menusImpl, "menus");
        events = Objects.requireNonNull(eventsImpl, "events");
    }

    public static OrganStorage storage() {
        return require(storage, "OrganStorage");
    }

    public static OrganNetwork network() {
        return require(network, "OrganNetwork");
    }

    public static OrganItemCodec itemCodec() {
        return require(itemCodec, "OrganItemCodec");
    }

    public static OrganMenuAccess menus() {
        return require(menus, "OrganMenuAccess");
    }

    public static OrganEvents events() {
        return require(events, "OrganEvents");
    }

    private static <T> T require(T value, String name) {
        if (value == null) {
            throw new IllegalStateException(name + " platform implementation not installed");
        }
        return value;
    }
}
