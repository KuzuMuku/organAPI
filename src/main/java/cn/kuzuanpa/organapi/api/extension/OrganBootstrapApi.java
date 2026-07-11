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

package cn.kuzuanpa.organapi.api.extension;

import cn.kuzuanpa.organapi.common.capability.IOrganHolder;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public final class OrganBootstrapApi {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final List<OrganHolderBootstrapper> BOOTSTRAPPERS = new CopyOnWriteArrayList<>();

    private OrganBootstrapApi() {
    }

    public static void register(@NotNull OrganHolderBootstrapper bootstrapper) {
        BOOTSTRAPPERS.add(bootstrapper);
    }

    /**
     * Runs registered organ bootstrappers exactly once per entity lifetime.
     * This bootstrap is meant to provide the entity's initial organ state when
     * the holder is first used in gameplay, typically when its organ UI is
     * opened for the first time. The resulting organ state is persistent: it is
     * saved with the holder and must continue to follow the entity until that
     * entity dies, rather than being rebuilt whenever the holder is reloaded
     * from NBT.
     */
    public static void bootstrap(@NotNull Entity entity, @NotNull IOrganHolder holder) {
        if (entity.level().isClientSide() || holder.isBootstrapInitialized()) {
            return;
        }
        holder.setBootstrapInitialized(true);
        for (OrganHolderBootstrapper bootstrapper : BOOTSTRAPPERS) {
            try {
                bootstrapper.bootstrap(entity, holder);
            } catch (Exception exception) {
                LOGGER.warn("Organ bootstrapper {} failed for {}: {}", bootstrapper.getClass().getName(), entity.getUUID(), exception.getMessage());
            }
        }
    }
}
