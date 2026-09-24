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

package cn.kuzuanpa.organapi.neoforge.client;

import cn.kuzuanpa.organapi.client.render.OrganItemModels;
import cn.kuzuanpa.organapi.common.item.OrganItem;
import cn.kuzuanpa.organapi.common.registry.OrganItems;
import cn.kuzuanpa.organapi.platform.Platform;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import org.jetbrains.annotations.Nullable;

/**
 * NeoForge 1.21.1 side of the data-driven organ renderer. Same design as the Forge
 * version; the API diffs are structural (own {@code ModelEvent}/{@code BakedModelWrapper}
 * packages, and {@code ModifyBakingResult} is keyed by {@link ModelResourceLocation}
 * not {@code ResourceLocation}), so this lives in the neoforge source set rather than
 * behind {@code //?}.
 */
public final class NeoForgeOrganItemModelSetup {
    private NeoForgeOrganItemModelSetup() {
    }

    public static void onRegisterAdditional(ModelEvent.RegisterAdditional event) {
        OrganItemModels.clear();
        for (ResourceLocation location : OrganItemModels.discoverModelLocations(
                net.minecraft.client.Minecraft.getInstance().getResourceManager())) {
            event.register(ModelResourceLocation.standalone(location));
        }
    }

    public static void onBakingCompleted(ModelEvent.ModifyBakingResult event) {
        var models = event.getModels();
        for (var entry : models.entrySet()) {
            ResourceLocation organId = OrganItemModels.organIdFromModel(entry.getKey().id());
            if (organId != null) {
                OrganItemModels.putBaked(organId, entry.getValue());
            }
        }
        ModelResourceLocation itemModelId = ModelResourceLocation.inventory(
                ResourceLocation.fromNamespaceAndPath("organapi", "organ_item"));
        BakedModel base = models.get(itemModelId);
        if (base != null) {
            models.put(itemModelId, new OrganItemBakedModel(base));
        }
    }

    private static final class OrganItemBakedModel extends BakedModelWrapper<BakedModel> {
        private final ItemOverrides overrides;

        OrganItemBakedModel(BakedModel original) {
            super(original);
            this.overrides = new OrganItemOverrides(original.getOverrides());
        }

        @Override
        public ItemOverrides getOverrides() {
            return overrides;
        }
    }

    private static final class OrganItemOverrides extends ItemOverrides {
        private final ItemOverrides delegate;

        OrganItemOverrides(ItemOverrides delegate) {
            this.delegate = delegate;
        }

        @Nullable
        @Override
        public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level,
                                  @Nullable LivingEntity entity, int seed) {
            if (stack.getItem() instanceof OrganItem && stack.getItem() == OrganItems.ORGAN_ITEM.get()) {
                ResourceLocation organId = Platform.itemIdentity().getOrganId(stack);
                BakedModel organModel = OrganItemModels.baked(organId);
                if (organModel != null) {
                    return organModel;
                }
            }
            return delegate.resolve(model, stack, level, entity, seed);
        }
    }
}
