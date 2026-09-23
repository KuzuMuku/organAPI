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

import cn.kuzuanpa.organapi.OrganApiMod;
import cn.kuzuanpa.organapi.common.capability.IOrganHolder;
import cn.kuzuanpa.organapi.common.capability.PlayerOrganHolder;
import cn.kuzuanpa.organapi.core.state.OrganStateCodec;
import cn.kuzuanpa.organapi.core.state.OrganStateData;
import java.util.function.Supplier;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * NeoForge organ-state attachment. Replaces the Forge Capability with an
 * {@link AttachmentType} that stores a {@link PlayerOrganHolder} on the entity.
 *
 * <p>Serialization reuses the step-3 loader-independent contract verbatim: the
 * holder is snapshotted to {@link OrganStateData} and encoded with
 * {@link OrganStateCodec} to a {@code byte[]}, then wrapped in a {@link ByteArrayTag}
 * for attachment persistence. The SAME codec bytes are what the S2C payload sends,
 * so save/load and network sync share one format. {@code copyOnDeath} carries the
 * organs across respawn (the Forge {@code PlayerEvent.Clone} equivalent).
 */
public final class NeoForgeOrganAttachment {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, OrganApiMod.MOD_ID);

    public static final Supplier<AttachmentType<PlayerOrganHolder>> ORGAN_HOLDER =
            ATTACHMENT_TYPES.register("organ_holder", () -> AttachmentType
                    .builder((IAttachmentHolder) -> new PlayerOrganHolder(asEntity(IAttachmentHolder)))
                    .serialize(new OrganHolderSerializer())
                    .copyOnDeath()
                    .build());

    private NeoForgeOrganAttachment() {
    }

    private static Entity asEntity(net.neoforged.neoforge.attachment.IAttachmentHolder holder) {
        // Organ holders are only attached to entities; the cast is always valid.
        return (Entity) holder;
    }

    /**
     * Bridges the attachment's {@link Tag} serializer to the loader-independent
     * {@link OrganStateCodec} bytes, keeping the holder's DTO contract identical to
     * the Forge side.
     */
    private static final class OrganHolderSerializer
            implements IAttachmentSerializer<ByteArrayTag, PlayerOrganHolder> {
        @Override
        public PlayerOrganHolder read(net.neoforged.neoforge.attachment.IAttachmentHolder holder,
                                      ByteArrayTag tag,
                                      net.minecraft.core.HolderLookup.Provider provider) {
            PlayerOrganHolder organHolder = new PlayerOrganHolder(asEntity(holder));
            organHolder.loadStateData(OrganStateCodec.decode(tag.getAsByteArray()));
            return organHolder;
        }

        @Override
        public ByteArrayTag write(PlayerOrganHolder attachment,
                                  net.minecraft.core.HolderLookup.Provider provider) {
            OrganStateData data = attachment.toStateData();
            return new ByteArrayTag(OrganStateCodec.encode(data));
        }
    }

    /** Marker id kept for parity with the Forge capability provider id. */
    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath(OrganApiMod.MOD_ID, "organ_holder");

    /** Resolves the holder as the common {@link IOrganHolder} interface. */
    public static IOrganHolder resolve(Entity entity) {
        return entity.getData(ORGAN_HOLDER);
    }
}
