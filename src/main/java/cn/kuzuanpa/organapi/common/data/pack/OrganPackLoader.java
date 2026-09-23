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

import com.mojang.logging.LogUtils;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.resource.DelegatingPackResources;
import net.minecraftforge.resource.PathPackResources;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * tacz-style custom pack loader. Loads organ definitions (and client assets)
 * from the game run directory instead of the mod jar's built-in datapack.
 *
 * <p>Layout convention:
 * <ul>
 *   <li>{@code <gameDir>/organs/data/<pack>/}      server data side ({@link PackType#SERVER_DATA})</li>
 *   <li>{@code <gameDir>/organs/resources/<pack>/} client resources side ({@link PackType#CLIENT_RESOURCES})</li>
 * </ul>
 * Each direct sub-directory is one sub-pack; they are aggregated into a single
 * built-in, always-enabled {@link Pack} at {@link Pack.Position#BOTTOM}.
 *
 * <p>One instance is created per repository (one for {@code SERVER_DATA}, one for
 * {@code CLIENT_RESOURCES}) with its {@link PackType} fixed at construction, so the
 * two repositories a client builds never share mutable state.
 */
public final class OrganPackLoader implements RepositorySource {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String ROOT_DIR = "organs";
    private static final String DATA_SUBDIR = "data";
    private static final String RESOURCES_SUBDIR = "resources";
    private static final String AGGREGATE_PACK_ID = "organapi_packs";

    private final PackType packType;

    public OrganPackLoader(PackType packType) {
        this.packType = packType;
    }

    @Override
    public void loadPacks(Consumer<Pack> onLoad) {
        Pack pack = discoverPacks();
        if (pack != null) {
            onLoad.accept(pack);
        }
    }

    @Nullable
    private Pack discoverPacks() {
        Path scanRoot = resolveScanRoot(this.packType);
        if (scanRoot == null) {
            return null;
        }

        List<PathPackResources> subPacks = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(scanRoot)) {
            for (Path entry : stream) {
                if (!Files.isDirectory(entry)) {
                    continue;
                }
                String packName = entry.getFileName().toString();
                LOGGER.info("[organapi] Found organ sub-pack '{}' ({})", packName, this.packType);
                subPacks.add(new PathPackResources(packName, false, entry));
            }
        } catch (IOException exception) {
            LOGGER.error("[organapi] Failed to scan organ packs in {}", scanRoot, exception);
            return null;
        }

        if (subPacks.isEmpty()) {
            LOGGER.info("[organapi] No organ sub-packs found in {}", scanRoot);
            return null;
        }

        PackType type = this.packType;
        return Pack.readMetaAndCreate(
                AGGREGATE_PACK_ID,
                Component.literal("organAPI Packs"),
                true,
                id -> new DelegatingPackResources(
                        id,
                        false,
                        new PackMetadataSection(
                                Component.translatable("organapi.packs.description"),
                                SharedConstants.getCurrentVersion().getPackVersion(type)),
                        subPacks),
                type,
                Pack.Position.BOTTOM,
                PackSource.BUILT_IN);
    }

    /**
     * Resolves the scan root for the given side and ensures both roots exist so
     * the folders show up on first launch. Returns null if creation failed.
     */
    @Nullable
    private static Path resolveScanRoot(PackType packType) {
        Path root = FMLPaths.GAMEDIR.get().resolve(ROOT_DIR);
        Path dataRoot = root.resolve(DATA_SUBDIR);
        Path resourcesRoot = root.resolve(RESOURCES_SUBDIR);
        try {
            Files.createDirectories(dataRoot);
            Files.createDirectories(resourcesRoot);
        } catch (IOException exception) {
            LOGGER.error("[organapi] Failed to create organs directories under {}", root, exception);
            return null;
        }
        return packType == PackType.CLIENT_RESOURCES ? resourcesRoot : dataRoot;
    }
}
