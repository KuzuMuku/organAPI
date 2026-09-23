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

package cn.kuzuanpa.organapi.common.capability;

import cn.kuzuanpa.organapi.api.extension.OrganBootstrapApi;
import cn.kuzuanpa.organapi.common.event.CommonForgeEvents;
import cn.kuzuanpa.organapi.core.state.OrganStateCodec;
import cn.kuzuanpa.organapi.core.state.OrganStateData;
import cn.kuzuanpa.organapi.platform.OrganStorage;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;

/**
 * Forge implementation of {@link OrganStorage}, backed by the entity
 * {@link OrganCapabilities#ORGAN_HOLDER} capability. The serialization half of
 * the contract goes through the loader-independent {@link OrganStateCodec}/
 * {@link OrganStateData}; step 4's NeoForge attachment port implements the same
 * interface with the same codec bytes.
 */
public final class ForgeOrganStorage implements OrganStorage {
    @Override
    public void register() {
        MinecraftForge.EVENT_BUS.register(new CommonForgeEvents());
    }

    @Override
    public Optional<IOrganHolder> get(Entity entity) {
        Optional<IOrganHolder> holder = entity.getCapability(OrganCapabilities.ORGAN_HOLDER).resolve();
        holder.ifPresent(value -> OrganBootstrapApi.bootstrap(entity, value));
        return holder;
    }

    @Override
    public Optional<IOrganHolder> resolve(Entity entity) {
        return entity.getCapability(OrganCapabilities.ORGAN_HOLDER).resolve();
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
