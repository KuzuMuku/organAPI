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
import cn.kuzuanpa.organapi.platform.Platform;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Organ carrier item. Two modes:
 * <ul>
 *   <li><b>Fixed</b> — {@code definitionId != null}: always represents one organ
 *       (legacy sample items).</li>
 *   <li><b>Generic/data-driven</b> — {@code definitionId == null}: the organ id is
 *       read per-{@link ItemStack} through the {@link cn.kuzuanpa.organapi.platform.OrganItemIdentity}
 *       seam (Forge NBT / NeoForge DataComponent), so a single registered
 *       {@code organapi:organ_item} backs every data-defined organ (tacz pattern).</li>
 * </ul>
 */
public class OrganItem extends Item {
    private static final String TRANSLATION_PREFIX = "lang:";
    @Nullable
    private final ResourceLocation definitionId;

    public OrganItem(Properties properties, @Nullable ResourceLocation definitionId) {
        super(properties);
        this.definitionId = definitionId;
    }

    @Nullable
    public ResourceLocation getDefinitionId(ItemStack stack) {
        if (definitionId != null) {
            return definitionId;
        }
        return Platform.itemIdentity().getOrganId(stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        if (definitionId == null) {
            ResourceLocation organId = Platform.itemIdentity().getOrganId(stack);
            if (organId != null) {
                // Data organs carry their own lang key: item.<ns>.<path>
                return Component.translatable("item." + organId.getNamespace() + "." + organId.getPath());
            }
        }
        return super.getName(stack);
    }

    @Override
    //? if forge {
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    //?} else {
    /*public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    *///?}
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
