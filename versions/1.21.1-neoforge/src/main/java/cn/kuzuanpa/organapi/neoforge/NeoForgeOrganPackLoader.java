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

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.CompositePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.neoforged.fml.loading.FMLPaths;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * NeoForge 1.21.1 port of the tacz-style custom pack loader (Forge
 * {@code OrganPackLoader}). Loads organ definitions/assets from
 * {@code <gameDir>/organs/{data,resources}/<pack>/} at run time.
 *
 * <p>The 1.21.1 pack-construction API differs from 1.20.1 (this is why the loader
 * lives per-loader, not behind {@code //?}): {@code PathPackResources} now takes a
 * {@link PackLocationInfo} instead of {@code (name, boolean, path)}; there is no
 * NeoForge {@code DelegatingPackResources}, so sub-packs are merged with vanilla
 * {@link CompositePackResources}; and {@link Pack#readMetaAndCreate} takes a
 * {@link PackLocationInfo} + {@link PackSelectionConfig} rather than the old
 * positional {@code (id, title, required, supplier, type, position, source)}.
 * The pack is built-in and required→always-enabled at {@link Pack.Position#BOTTOM},
 * reproducing the step-1 behavior.
 */
public final class NeoForgeOrganPackLoader implements RepositorySource {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String ROOT_DIR = "organs";
    private static final String DATA_SUBDIR = "data";
    private static final String RESOURCES_SUBDIR = "resources";
    private static final String AGGREGATE_PACK_ID = "organapi_packs";

    private final PackType packType;

    public NeoForgeOrganPackLoader(PackType packType) {
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

        PackLocationInfo location = new PackLocationInfo(
                AGGREGATE_PACK_ID,
                Component.literal("organAPI Packs"),
                PackSource.BUILT_IN,
                Optional.empty());

        List<PackResources> subPacks = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(scanRoot)) {
            for (Path entry : stream) {
                if (!Files.isDirectory(entry)) {
                    continue;
                }
                String packName = entry.getFileName().toString();
                LOGGER.info("[organapi] Found organ sub-pack '{}' ({})", packName, this.packType);
                PackLocationInfo subLocation = new PackLocationInfo(
                        packName, Component.literal(packName), PackSource.BUILT_IN, Optional.empty());
                subPacks.add(new PathPackResources(subLocation, entry));
            }
        } catch (IOException exception) {
            LOGGER.error("[organapi] Failed to scan organ packs in {}", scanRoot, exception);
            return null;
        }

        if (subPacks.isEmpty()) {
            LOGGER.info("[organapi] No organ sub-packs found in {}", scanRoot);
            return null;
        }

        Pack.ResourcesSupplier supplier = new Pack.ResourcesSupplier() {
            @Override
            public PackResources openPrimary(PackLocationInfo loc) {
                return aggregate(subPacks);
            }

            @Override
            public PackResources openFull(PackLocationInfo loc, Pack.Metadata metadata) {
                return aggregate(subPacks);
            }
        };

        PackSelectionConfig selection = new PackSelectionConfig(true, Pack.Position.BOTTOM, false);
        return Pack.readMetaAndCreate(location, supplier, this.packType, selection);
    }

    private static PackResources aggregate(List<PackResources> subPacks) {
        // Merge every discovered sub-pack into one composite; the first acts as the
        // primary and the rest stack behind it.
        PackResources primary = subPacks.get(0);
        List<PackResources> rest = subPacks.subList(1, subPacks.size());
        return new CompositePackResources(primary, new ArrayList<>(rest));
    }

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
