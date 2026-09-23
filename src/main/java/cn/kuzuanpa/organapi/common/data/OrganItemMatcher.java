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

import cn.kuzuanpa.organapi.core.model.BodyPartDefinition;
import cn.kuzuanpa.organapi.core.model.OrganId;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Platform helper that applies a core {@link BodyPartDefinition}'s accepted-tag
 * rule to a Minecraft {@link ItemStack}. This is the MC-side of the former
 * {@code BodyPartDefinition#accepts}, kept out of the core because it needs
 * {@code ItemStack}/{@code TagKey}.
 */
public final class OrganItemMatcher {
    private static final TagKey<Item> ORGAN_TAG =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath("organapi", "organs"));

    private OrganItemMatcher() {
    }

    public static boolean accepts(BodyPartDefinition definition, ItemStack stack) {
        if (definition.acceptedTags().isEmpty() || !stack.is(ORGAN_TAG)) {
            return true;
        }
        for (OrganId tagId : definition.acceptedTags()) {
            if (stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath(tagId.namespace(), tagId.path())))) {
                return true;
            }
        }
        return false;
    }
}
