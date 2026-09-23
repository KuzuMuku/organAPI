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

package cn.kuzuanpa.organapi.common.data;

import cn.kuzuanpa.organapi.common.item.OrganItem;
import cn.kuzuanpa.organapi.core.model.BodyPartDefinition;
import cn.kuzuanpa.organapi.core.model.OrganDefinition;
import cn.kuzuanpa.organapi.core.model.OrganId;
import cn.kuzuanpa.organapi.platform.OrganIds;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * In-memory registry of the parsed core definitions. This is a platform class:
 * it stores core ({@link OrganId}-keyed) models and adapts them to the
 * {@code ResourceLocation}/{@code ItemStack} types the rest of the Forge code
 * uses at its boundary.
 */
public final class OrganRegistryAccess {
    private static final Map<OrganId, BodyPartDefinition> BODY_PARTS = new ConcurrentHashMap<>();
    private static final Map<OrganId, OrganDefinition> ORGANS = new ConcurrentHashMap<>();
    private static final Map<OrganId, OrganDefinition> ORGANS_BY_ITEM = new ConcurrentHashMap<>();

    private OrganRegistryAccess() {
    }

    public static Collection<BodyPartDefinition> getBodyParts() {
        return BODY_PARTS.values().stream().sorted(Comparator.comparingInt(BodyPartDefinition::sortOrder)).toList();
    }

    public static List<ResourceLocation> getOrderedBodyPartIds() {
        return getBodyParts().stream().map(BodyPartDefinition::id).map(OrganIds::toResourceLocation).toList();
    }

    public static Optional<BodyPartDefinition> getBodyPart(ResourceLocation id) {
        return getBodyPart(OrganIds.toOrganId(id));
    }

    public static Optional<BodyPartDefinition> getBodyPart(OrganId id) {
        return Optional.ofNullable(BODY_PARTS.get(id));
    }

    public static Collection<OrganDefinition> getOrgans() {
        return ORGANS.values();
    }

    public static Optional<OrganDefinition> getOrgan(ResourceLocation id) {
        return getOrgan(OrganIds.toOrganId(id));
    }

    public static Optional<OrganDefinition> getOrgan(OrganId id) {
        return Optional.ofNullable(ORGANS.get(id));
    }

    public static Optional<OrganDefinition> getOrgan(ItemStack stack) {
        if (stack.getItem() instanceof OrganItem organItem) {
            return getOrgan(organItem.getDefinitionId(stack));
        }
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return itemId == null ? Optional.empty() : Optional.ofNullable(ORGANS_BY_ITEM.get(OrganIds.toOrganId(itemId)));
    }

    public static void replaceBodyParts(Map<OrganId, BodyPartDefinition> replacements) {
        BODY_PARTS.clear();
        BODY_PARTS.putAll(replacements);
    }

    public static void replaceOrgans(Map<OrganId, OrganDefinition> replacements, Map<OrganId, OrganDefinition> itemReplacements) {
        ORGANS.clear();
        ORGANS.putAll(replacements);
        ORGANS_BY_ITEM.clear();
        ORGANS_BY_ITEM.putAll(itemReplacements);
    }
}
