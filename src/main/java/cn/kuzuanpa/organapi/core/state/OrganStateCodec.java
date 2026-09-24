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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Self-describing binary codec for {@link OrganStateData}. Used for network
 * transport (Forge {@code SimpleChannel} today, NeoForge {@code StreamCodec}
 * payload tomorrow) so the wire format is loader-independent.
 *
 * <p>On-disk persistence stays the platform's own NBT layout; this codec is only
 * for the {@code OrganStorage} snapshot exchange.
 */
public final class OrganStateCodec {
    private static final int VERSION = 1;

    private OrganStateCodec() {
    }

    public static byte[] encode(OrganStateData data) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(buffer)) {
            out.writeInt(VERSION);
            out.writeBoolean(data.isBootstrapInitialized());
            out.writeInt(data.parts().size());
            for (OrganStateData.PartState part : data.parts()) {
                out.writeUTF(part.bodyPartId().toString());
                out.writeInt(part.bonusCapacity());
                out.writeInt(part.slots().size());
                for (OrganStateData.SlotData slot : part.slots()) {
                    out.writeInt(slot.slot());
                    out.writeInt(slot.item().length);
                    out.write(slot.item());
                }
            }
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
        return buffer.toByteArray();
    }

    public static OrganStateData decode(byte[] bytes) {
        OrganStateData data = new OrganStateData();
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes))) {
            in.readInt(); // version; only VERSION exists today
            data.setBootstrapInitialized(in.readBoolean());
            int partCount = in.readInt();
            for (int i = 0; i < partCount; i++) {
                OrganId bodyPartId = OrganId.parse(in.readUTF());
                int bonusCapacity = in.readInt();
                OrganStateData.PartState part = data.addPart(bodyPartId, bonusCapacity);
                int slotCount = in.readInt();
                for (int s = 0; s < slotCount; s++) {
                    int slot = in.readInt();
                    int length = in.readInt();
                    byte[] item = new byte[length];
                    in.readFully(item);
                    part.addSlot(slot, item);
                }
            }
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
        return data;
    }
}
