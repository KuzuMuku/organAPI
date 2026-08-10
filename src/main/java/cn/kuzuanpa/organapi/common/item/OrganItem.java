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

package cn.kuzuanpa.organapi.common.item;

import cn.kuzuanpa.organapi.common.data.OrganRegistryAccess;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class OrganItem extends Item {
    private static final String TRANSLATION_PREFIX = "lang:";
    private final ResourceLocation definitionId;

    public OrganItem(Properties properties, ResourceLocation definitionId) {
        super(properties);
        this.definitionId = definitionId;
    }

    public ResourceLocation getDefinitionId(ItemStack stack) {
        return definitionId;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
        OrganRegistryAccess.getOrgan(stack).ifPresent(definition -> definition.tooltips().stream()
                .map(OrganItem::tooltipComponent)
                .forEach(tooltipComponents::add));
    }

    private static Component tooltipComponent(String tooltip) {
        if (tooltip.startsWith(TRANSLATION_PREFIX)) {
            return Component.translatable(tooltip.substring(TRANSLATION_PREFIX.length()));
        }
        return Component.literal(tooltip);
    }
}
