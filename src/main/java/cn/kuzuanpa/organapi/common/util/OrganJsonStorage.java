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

package cn.kuzuanpa.organapi.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;

public final class OrganJsonStorage {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Object FILE_LOCK = new Object();
    private static final int FORMAT = 1;

    private OrganJsonStorage() {
    }

    /**
     * Loads one module section from the per-UUID organ save file.
     * OrganAPI uses "organapi"; OrganEffects uses "organeffects".
     */
    public static CompoundTag load(Entity owner, String section) {
        synchronized (FILE_LOCK) {
            Path path = pathFor(owner);
            if (path == null || !Files.isRegularFile(path)) {
                return new CompoundTag();
            }
            try {
                JsonElement root = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8));
                if (root instanceof JsonObject object && object.has(section)) {
                    return jsonToNbt(object.get(section));
                }
            } catch (Exception exception) {
                LOGGER.warn("Failed to read organ JSON {}: {}", path, exception.toString());
            }
            return new CompoundTag();
        }
    }

    /**
     * Writes one module section into the shared per-UUID organ save file.
     * Existing sections written by other modules are preserved.
     */
    public static boolean save(Entity owner, String section, CompoundTag data) {
        synchronized (FILE_LOCK) {
            Path path = pathFor(owner);
            if (path == null) {
                return false;
            }
            try {
                if (path.getParent() != null) {
                    Files.createDirectories(path.getParent());
                }
                JsonObject root = new JsonObject();
                root.addProperty("format", FORMAT);
                if (Files.isRegularFile(path)) {
                    try {
                        JsonElement existing = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8));
                        if (existing instanceof JsonObject existingObject) {
                            root = existingObject;
                        }
                    } catch (Exception exception) {
                        LOGGER.warn("Failed to read existing organ JSON {}; rewriting it: {}", path, exception.toString());
                    }
                }
                root.addProperty("format", FORMAT);
                root.add(section, NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, data));
                Path temporary = path.resolveSibling(path.getFileName() + ".tmp");
                Files.writeString(temporary, GSON.toJson(root), StandardCharsets.UTF_8);
                try {
                    Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException exception) {
                    Files.deleteIfExists(temporary);
                    throw exception;
                }
                return true;
            } catch (Exception exception) {
                LOGGER.error("Failed to save organ JSON {}: {}", path, exception.toString());
                return false;
            }
        }
    }

    private static Path pathFor(Entity owner) {
        MinecraftServer server = owner.getServer();
        if (server == null) {
            return null;
        }
        String category = owner instanceof Player ? "player" : "entity";
        return server.getWorldPath(LevelResource.ROOT)
                .resolve("organAPI")
                .resolve(category)
                .resolve(owner.getUUID() + ".json");
    }

    private static CompoundTag jsonToNbt(JsonElement element) {
        Tag tag = JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, element);
        return tag instanceof CompoundTag compound ? compound : new CompoundTag();
    }
}
