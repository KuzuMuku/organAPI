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

import cn.kuzuanpa.organapi.common.capability.IOrganHolder;
import java.util.Optional;
import net.minecraft.world.entity.Entity;

/**
 * Entity organ-state access seam. Forge backs this with a Capability; step 4's
 * NeoForge port will back it with an Attachment. Callers never touch the loader
 * mechanism directly.
 *
 * <p>The serialization half of the contract is intentionally
 * {@code byte[]}-based (produced/consumed via
 * {@link cn.kuzuanpa.organapi.core.state.OrganStateCodec} over
 * {@link cn.kuzuanpa.organapi.core.state.OrganStateData}), so the same wire
 * bytes move across both loaders without a {@code CompoundTag} dependency.
 */
public interface OrganStorage {
    /** Registers the loader-specific attachment hooks (capability event, etc.). */
    void register();

    /** Resolves the holder and runs one-time bootstrap (gameplay entry point). */
    Optional<IOrganHolder> get(Entity entity);

    /** Resolves the holder without triggering bootstrap. */
    Optional<IOrganHolder> resolve(Entity entity);

    /** Snapshots the entity's organ state into loader-independent codec bytes. */
    byte[] serialize(Entity entity);

    /** Applies a codec-bytes snapshot onto the entity's holder (client sync). */
    void apply(Entity entity, byte[] snapshot);
}
