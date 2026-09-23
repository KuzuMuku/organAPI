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

package cn.kuzuanpa.organapi.common.data.pack;

import cn.kuzuanpa.organapi.OrganApiMod;
import com.mojang.logging.LogUtils;
//? if forge {
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
//?} else {
/*import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
*///?}
import org.slf4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

/**
 * Shared helper for organ add-on mods to ship a default custom pack that gets
 * extracted from the jar into {@code <gameDir>/organs/{data,resources}/<packName>/}
 * on first launch, so the loader-specific pack source can serve it.
 *
 * <p>Templates must live in the mod jar at
 * {@code /pack_templates/data/<packName>/} and
 * {@code /pack_templates/resources/<packName>/}. These roots sit outside
 * {@code data/} and {@code assets/}, so vanilla never auto-loads them; they are
 * pure copy sources. Either side may be absent.
 *
 * <p>Copy is skipped if the destination already exists, so user edits are never
 * clobbered. Delete the folder to get a fresh copy.
 */
public final class OrganPackTemplateExtractor {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String SAMPLE_PACK_NAME = "organapi_samples";

    private OrganPackTemplateExtractor() {
    }

    /** Extracts organAPI's own bundled sample pack. */
    public static void extractDefaultsIfAbsent() {
        extractPackIfAbsent(OrganApiMod.MOD_ID, SAMPLE_PACK_NAME);
    }

    /**
     * Extracts the data and resources templates for {@code packName} bundled in
     * mod {@code modId}'s jar into the game run directory, if not already present.
     *
     * @param modId    the owning mod id (used to locate the jar)
     * @param packName the sub-pack directory name under {@code pack_templates/{data,resources}/}
     */
    public static void extractPackIfAbsent(String modId, String packName) {
        Path organsRoot = FMLPaths.GAMEDIR.get().resolve("organs");
        copyTemplate(modId, "pack_templates/data/" + packName,
                organsRoot.resolve("data").resolve(packName));
        copyTemplate(modId, "pack_templates/resources/" + packName,
                organsRoot.resolve("resources").resolve(packName));
    }

    private static void copyTemplate(String modId, String jarRelativePath, Path destination) {
        if (Files.exists(destination)) {
            return;
        }
        Path source = ModList.get()
                .getModFileById(modId)
                .getFile()
                .findResource(jarRelativePath);
        if (source == null || !Files.exists(source)) {
            LOGGER.debug("[organapi] Pack template '{}' not found in {} jar; skipping extraction", jarRelativePath, modId);
            return;
        }
        LOGGER.info("[organapi] Extracting pack template to {}", destination);
        try (Stream<Path> walk = Files.walk(source)) {
            walk.forEach(path -> copyEntry(source, path, destination));
        } catch (IOException | UncheckedIOException exception) {
            LOGGER.error("[organapi] Failed to extract pack template to {}", destination, exception);
        }
    }

    private static void copyEntry(Path sourceRoot, Path entry, Path destinationRoot) {
        // Relativize within the jar filesystem, then re-root against the on-disk destination.
        Path target = destinationRoot.resolve(sourceRoot.relativize(entry).toString());
        try {
            if (Files.isDirectory(entry)) {
                Files.createDirectories(target);
            } else {
                Files.createDirectories(target.getParent());
                Files.copy(entry, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }
}
