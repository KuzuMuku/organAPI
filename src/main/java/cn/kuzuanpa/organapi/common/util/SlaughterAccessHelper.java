package cn.kuzuanpa.organapi.common.util;

import cn.kuzuanpa.organapi.api.body.BodyPartIds;
import cn.kuzuanpa.organapi.common.config.OrganApiConfig;
import cn.kuzuanpa.organapi.common.menu.OrganOverviewMenu;
import java.util.Comparator;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;

public final class SlaughterAccessHelper {
    private SlaughterAccessHelper() {
    }

    public static boolean canOpenChestCavity(LivingEntity entity) {
        float maxHealth = entity.getMaxHealth();
        float threshold = OrganApiConfig.getSlaughterHealthThresholdRatio();
        return entity.isAlive() && maxHealth > 0.0F && entity.getHealth() / maxHealth <= threshold;
    }

    public static Optional<LivingEntity> findLivingEntityDirectlyAbove(Level level, BlockPos pos) {
        AABB searchBox = new AABB(
                pos.getX(), pos.getY() + 1.0D, pos.getZ(),
                pos.getX() + 1.0D, pos.getY() + 2.2D, pos.getZ() + 1.0D);
        return level.getEntitiesOfClass(LivingEntity.class, searchBox, LivingEntity::isAlive).stream()
                .min(Comparator
                        .comparingDouble((LivingEntity entity) -> entity.distanceToSqr(pos.getCenter()))
                        .thenComparingDouble(LivingEntity::getY));
    }

    public static void applyOpenedChestRestriction(LivingEntity target) {
        int duration = OrganApiConfig.getSlaughterRestrictionDurationTicks();
        int slownessAmplifier = OrganApiConfig.getSlaughterSlownessAmplifier();
        int weaknessAmplifier = OrganApiConfig.getSlaughterWeaknessAmplifier();
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, slownessAmplifier, false, true));
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, weaknessAmplifier, false, true));
    }

    public static void openOverview(ServerPlayer viewer, LivingEntity target) {
        NetworkHooks.openScreen(viewer, new SimpleMenuProvider(
                (windowId, inventory, entityPlayer) -> new OrganOverviewMenu(windowId, inventory, target.getId(), BodyPartIds.CHEST),
                Component.translatable("menu.organapi.organ_overview")), buf -> {
            buf.writeInt(target.getId());
            buf.writeResourceLocation(BodyPartIds.CHEST);
        });
    }
}
