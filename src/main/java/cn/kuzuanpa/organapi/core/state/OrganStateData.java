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

package cn.kuzuanpa.organapi.core.state;

import cn.kuzuanpa.organapi.core.model.OrganId;
import java.util.ArrayList;
import java.util.List;

/**
 * Version-agnostic snapshot of an entity's organ state, used as the transport
 * DTO for the {@code OrganStorage} platform contract (both network sync and,
 * in step 4, the NeoForge attachment port).
 *
 * <p>Deliberately Minecraft-free: an installed organ is carried as an opaque
 * {@code byte[]} of platform-serialized item data ({@link SlotData#item()}).
 * The platform (Forge capability today, NeoForge attachment tomorrow) owns the
 * item↔bytes encoding, so this DTO never touches {@code ItemStack}/{@code CompoundTag}.
 * Empty slots are omitted, matching the original NBT layout.
 */
public final class OrganStateData {
    private boolean bootstrapInitialized;
    private final List<PartState> parts = new ArrayList<>();

    public boolean isBootstrapInitialized() {
        return bootstrapInitialized;
    }

    public void setBootstrapInitialized(boolean bootstrapInitialized) {
        this.bootstrapInitialized = bootstrapInitialized;
    }

    public List<PartState> parts() {
        return parts;
    }

    public PartState addPart(OrganId bodyPartId, int bonusCapacity) {
        PartState state = new PartState(bodyPartId, bonusCapacity);
        parts.add(state);
        return state;
    }

    /** Per-body-part slice of the snapshot. */
    public static final class PartState {
        private final OrganId bodyPartId;
        private int bonusCapacity;
        private final List<SlotData> slots = new ArrayList<>();

        public PartState(OrganId bodyPartId, int bonusCapacity) {
            this.bodyPartId = bodyPartId;
            this.bonusCapacity = bonusCapacity;
        }

        public OrganId bodyPartId() {
            return bodyPartId;
        }

        public int bonusCapacity() {
            return bonusCapacity;
        }

        public void setBonusCapacity(int bonusCapacity) {
            this.bonusCapacity = bonusCapacity;
        }

        public List<SlotData> slots() {
            return slots;
        }

        public void addSlot(int slot, byte[] item) {
            slots.add(new SlotData(slot, item));
        }
    }

    /** One occupied organ slot: index + opaque platform item bytes. */
    public record SlotData(int slot, byte[] item) {
    }
}
