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

import cn.kuzuanpa.organapi.api.OrganApi;
import cn.kuzuanpa.organapi.api.query.OrganQueryService;
import cn.kuzuanpa.organapi.core.model.BodyPartDefinition;
import cn.kuzuanpa.organapi.platform.OrganIds;
//? if forge {
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.UUID;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
//?} else {
/*import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
*///?}
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScalpelItem extends Item {
    //? if forge {
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("0d8d11c5-fb6a-4a6b-ae2c-5b59e12f0aa1");
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("e18a39b3-7e59-43d2-b7d0-bf9050bc0ed2");
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;
    //?}

    public ScalpelItem(Properties properties) {
        //? if forge {
        super(properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ATTACK_DAMAGE_UUID, "Weapon modifier", 1.0D, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED,
                new AttributeModifier(ATTACK_SPEED_UUID, "Weapon modifier", -2.0D, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
        //?} else {
        /*super(properties.attributes(ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 1.0D, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.0D, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build()));
        *///?}
    }

    //? if forge {
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return slot == EquipmentSlot.MAINHAND ? defaultModifiers : super.getAttributeModifiers(slot, stack);
    }
    //?}

    @Override
    //? if forge {
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    //?} else {
    /*public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    *///?}
        tooltipComponents.add(Component.translatable("tooltip.organapi.scalpel"));
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (!target.level().isClientSide && !target.isAlive()) {
            dropOrgans(target);
        }
        return result;
    }

    private void dropOrgans(LivingEntity target) {
        for (BodyPartDefinition bodyPart : OrganApi.getBodyParts()) {
            net.minecraft.resources.ResourceLocation bodyPartId = OrganIds.toResourceLocation(bodyPart.id());
            int organAmount = OrganQueryService.getInstalledOrgans(target, bodyPartId).size();
            for (int slotIndex = 0; slotIndex < organAmount; slotIndex++) {
                ItemStack removed = OrganApi.removeOrgan(target, bodyPartId, slotIndex);
                if (!removed.isEmpty()) {
                    target.level().addFreshEntity(new ItemEntity(target.level(), target.getX(), target.getY(), target.getZ(), removed));
                }
            }
        }
    }
}
