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
import cn.kuzuanpa.organapi.api.body.BodyPartDefinition;
import cn.kuzuanpa.organapi.api.query.OrganQueryService;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.UUID;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ScalpelItem extends Item {
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("0d8d11c5-fb6a-4a6b-ae2c-5b59e12f0aa1");
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("e18a39b3-7e59-43d2-b7d0-bf9050bc0ed2");
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public ScalpelItem(Properties properties) {
        super(properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ATTACK_DAMAGE_UUID, "Weapon modifier", 1.0D, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED,
                new AttributeModifier(ATTACK_SPEED_UUID, "Weapon modifier", -2.0D, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return slot == EquipmentSlot.MAINHAND ? defaultModifiers : super.getAttributeModifiers(slot, stack);
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
            int organAmount = OrganQueryService.getInstalledOrgans(target, bodyPart.id()).size();
            for (int slotIndex = 0; slotIndex < organAmount; slotIndex++) {
                ItemStack removed = OrganApi.removeOrgan(target, bodyPart.id(), slotIndex);
                if (!removed.isEmpty()) {
                    target.level().addFreshEntity(new ItemEntity(target.level(), target.getX(), target.getY(), target.getZ(), removed));
                }
            }
        }
    }
}
