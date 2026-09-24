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

package cn.kuzuanpa.organapi.core.model;

import java.util.List;

/**
 * Version-agnostic body-part definition. Mirror of the former platform
 * {@code cn.kuzuanpa.organapi.api.body.BodyPartDefinition} with all Minecraft
 * coupling removed:
 * <ul>
 *   <li>{@code ResourceLocation} → {@link OrganId}</li>
 *   <li>{@code List<TagKey<Item>> acceptedTags} → {@code List<OrganId> acceptedTags}
 *       (tag ids as plain identifiers; the platform resolves them to real tags)</li>
 *   <li>{@code getDisplayName()}/{@code accepts(ItemStack)} moved to the platform
 *       layer, which has access to {@code Component} / {@code ItemStack}.</li>
 * </ul>
 */
public record BodyPartDefinition(
        OrganId id,
        String translationKey,
        int defaultCapacity,
        Integer maxCapacity,
        int sortOrder,
        List<OrganId> acceptedTags,
        float visualWidthRatio,
        float visualHeightRatio,
        OverviewArea overviewArea
) {
    /** Default organ tag every body part accepts when it declares no explicit tags. */
    public static final OrganId ORGAN_TAG = OrganId.of("organapi", "organs");

    public BodyPartDefinition {
        defaultCapacity = Math.max(0, defaultCapacity);
        if (maxCapacity != null) {
            maxCapacity = Math.max(defaultCapacity, maxCapacity);
        }
        acceptedTags = List.copyOf(acceptedTags);
        visualWidthRatio = Math.max(0.1F, visualWidthRatio);
        visualHeightRatio = Math.max(0.1F, visualHeightRatio);
        if (overviewArea != null) {
            overviewArea = new OverviewArea(overviewArea.x(), overviewArea.y(), Math.max(1, overviewArea.width()), Math.max(1, overviewArea.height()));
        }
    }

    public static BodyPartDefinition simple(OrganId id, int defaultCapacity, int sortOrder) {
        return new BodyPartDefinition(id, "body_part." + id.namespace() + "." + id.path(), defaultCapacity, null, sortOrder,
                List.of(ORGAN_TAG), 1.0F, 1.0F, null);
    }

    public record OverviewArea(int x, int y, int width, int height) {
    }
}
