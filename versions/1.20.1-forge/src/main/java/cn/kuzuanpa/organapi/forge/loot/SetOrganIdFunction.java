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

package cn.kuzuanpa.organapi.forge.loot;

import cn.kuzuanpa.organapi.OrganApiMod;
import cn.kuzuanpa.organapi.platform.Platform;
import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.registries.DeferredRegister;
import java.util.function.Supplier;

/**
 * Loot function {@code organapi:set_organ_id}: stamps an organ definition id onto the
 * generic {@link cn.kuzuanpa.organapi.common.registry.OrganItems#ORGAN_ITEM} stack via the
 * {@code OrganItemIdentity} seam, so data-driven organs (which register no item of their own)
 * can be placed in vanilla/datapack loot tables and KubeJS LootJS entries. JSON:
 * {@code { "function": "organapi:set_organ_id", "organ_id": "vanillanether:scorch_throat_sac" }}
 * attached to an {@code organapi:organ_item} entry.
 *
 * <p>Forge-only: the 1.21 loot API replaced the JSON {@code Serializer} with a {@code MapCodec},
 * so a neoforge port would be a separate class. Drops/surgery are forge-only by design.
 */
public final class SetOrganIdFunction extends LootItemConditionalFunction {
    public static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTIONS =
            DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, OrganApiMod.MOD_ID);

    public static final Supplier<LootItemFunctionType> TYPE =
            LOOT_FUNCTIONS.register("set_organ_id", () -> new LootItemFunctionType(new Serializer()));

    private final ResourceLocation organId;

    private SetOrganIdFunction(LootItemCondition[] predicates, ResourceLocation organId) {
        super(predicates);
        this.organId = organId;
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        if (organId != null) {
            Platform.itemIdentity().setOrganId(stack, organId);
        }
        return stack;
    }

    @Override
    public LootItemFunctionType getType() {
        return TYPE.get();
    }

    public static final class Serializer extends LootItemConditionalFunction.Serializer<SetOrganIdFunction> {
        @Override
        public void serialize(JsonObject json, SetOrganIdFunction function, JsonSerializationContext ctx) {
            super.serialize(json, function, ctx);
            if (function.organId != null) {
                json.addProperty("organ_id", function.organId.toString());
            }
        }

        @Override
        public SetOrganIdFunction deserialize(JsonObject json, JsonDeserializationContext ctx,
                                              LootItemCondition[] predicates) {
            ResourceLocation organId = ResourceLocation.tryParse(GsonHelper.getAsString(json, "organ_id"));
            return new SetOrganIdFunction(predicates, organId);
        }
    }
}
