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

package cn.kuzuanpa.organapi.client.render;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * Loader-neutral support for the data-driven organ item renderer (tacz
 * {@code GunItemRendererWrapper} pattern, simplified to flat item models).
 *
 * <p>The single registered {@code organapi:organ_item} has no baked appearance of
 * its own; each concrete organ supplies a model at
 * {@code assets/<ns>/models/organ/<path>.json} inside its content pack. This class:
 * <ul>
 *   <li>discovers those model files from the {@link ResourceManager} at bake time
 *       ({@link #discoverModelLocations}), so the loader can register them as
 *       additional models to be baked;</li>
 *   <li>caches the baked results keyed by organ id ({@link #putBaked}), populated by
 *       the loader once baking completes;</li>
 *   <li>resolves the baked model for a given organ id at render time
 *       ({@link #baked}).</li>
 * </ul>
 * The per-loader {@code BakedModelWrapper} and the {@code ModelEvent} wiring live in
 * each version source set (their base class / event signatures differ); everything
 * data-shaped stays here.
 */
public final class OrganItemModels {
    /** Convention directory (under {@code assets/<ns>/}) holding per-organ item models. */
    public static final String MODEL_DIR = "models/organ";

    private static final Map<ResourceLocation, BakedModel> BAKED = new LinkedHashMap<>();

    private OrganItemModels() {
    }

    /**
     * The bakeable model location for an organ id: {@code <ns>:organ/<path>}. The
     * {@code organ/} prefix keeps these out of the vanilla {@code item/} model tree
     * (they are baked on demand, not auto-registered per item).
     */
    public static ResourceLocation modelLocationFor(ResourceLocation organId) {
        return ResourceLocation.fromNamespaceAndPath(organId.getNamespace(), "organ/" + organId.getPath());
    }

    /** Inverse of {@link #modelLocationFor}: model location → organ id, or null if not ours. */
    public static ResourceLocation organIdFromModel(ResourceLocation modelLocation) {
        String path = modelLocation.getPath();
        if (!path.startsWith("organ/")) {
            return null;
        }
        return ResourceLocation.fromNamespaceAndPath(modelLocation.getNamespace(), path.substring("organ/".length()));
    }

    /**
     * Scans the resource manager for every {@code models/organ/**.json} across all
     * namespaces and returns their bakeable model locations. Called from the loader's
     * additional-model registration event.
     */
    public static java.util.List<ResourceLocation> discoverModelLocations(ResourceManager resourceManager) {
        java.util.List<ResourceLocation> locations = new java.util.ArrayList<>();
        resourceManager.listResources(MODEL_DIR, path -> path.getPath().endsWith(".json")).keySet()
                .forEach(fileId -> {
                    String path = fileId.getPath();
                    // strip leading "models/" and trailing ".json" -> "organ/<path>"
                    String modelPath = path.substring("models/".length(), path.length() - ".json".length());
                    locations.add(ResourceLocation.fromNamespaceAndPath(fileId.getNamespace(), modelPath));
                });
        return locations;
    }

    public static void clear() {
        BAKED.clear();
    }

    public static void putBaked(ResourceLocation organId, BakedModel model) {
        BAKED.put(organId, model);
    }

    public static BakedModel baked(ResourceLocation organId) {
        return organId == null ? null : BAKED.get(organId);
    }
}
