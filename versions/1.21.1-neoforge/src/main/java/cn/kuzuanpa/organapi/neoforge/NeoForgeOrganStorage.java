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

package cn.kuzuanpa.organapi.neoforge;

import cn.kuzuanpa.organapi.api.extension.OrganBootstrapApi;
import cn.kuzuanpa.organapi.common.capability.IOrganHolder;
import cn.kuzuanpa.organapi.common.capability.PlayerOrganHolder;
import cn.kuzuanpa.organapi.core.state.OrganStateCodec;
import cn.kuzuanpa.organapi.core.state.OrganStateData;
import cn.kuzuanpa.organapi.platform.OrganStorage;
import java.util.Optional;
import net.minecraft.world.entity.Entity;

/**
 * NeoForge implementation of {@link OrganStorage}, backed by the
 * {@link NeoForgeOrganAttachment#ORGAN_HOLDER} attachment instead of a Forge
 * capability. The serialization half reuses the loader-independent
 * {@link OrganStateCodec}/{@link OrganStateData} contract byte-for-byte, so the
 * same wire/attachment bytes are valid on both loaders.
 *
 * <p>Persistence is automatic: the attachment serializer (see
 * {@link NeoForgeOrganAttachment}) writes with the entity NBT and copies on death.
 * The S2C sync path is separate and handled by {@code NeoForgeOrganNetwork}.
 */
public final class NeoForgeOrganStorage implements OrganStorage {
    @Override
    public void register() {
        // Attachment type is registered via its DeferredRegister on the mod bus in
        // the entry class; there is no per-entity attach event to subscribe here.
    }

    @Override
    public Optional<IOrganHolder> get(Entity entity) {
        Optional<IOrganHolder> holder = resolve(entity);
        holder.ifPresent(value -> OrganBootstrapApi.bootstrap(entity, value));
        return holder;
    }

    @Override
    public Optional<IOrganHolder> resolve(Entity entity) {
        // getData lazily creates the default holder via the attachment builder.
        return Optional.ofNullable(NeoForgeOrganAttachment.resolve(entity));
    }

    @Override
    public byte[] serialize(Entity entity) {
        OrganStateData data = resolve(entity)
                .filter(holder -> holder instanceof PlayerOrganHolder)
                .map(holder -> ((PlayerOrganHolder) holder).toStateData())
                .orElseGet(OrganStateData::new);
        return OrganStateCodec.encode(data);
    }

    @Override
    public void apply(Entity entity, byte[] snapshot) {
        OrganStateData data = OrganStateCodec.decode(snapshot);
        resolve(entity)
                .filter(holder -> holder instanceof PlayerOrganHolder)
                .ifPresent(holder -> ((PlayerOrganHolder) holder).loadStateData(data));
    }
}
