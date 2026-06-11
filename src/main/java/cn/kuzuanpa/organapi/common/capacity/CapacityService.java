package cn.kuzuanpa.organapi.common.capacity;

import cn.kuzuanpa.organapi.common.capability.IOrganHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public final class CapacityService {
    private CapacityService() {
    }

    public static int getPersistentBonus(Player player, ResourceLocation bodyPartId) {
        return IOrganHolder.resolve(player).map(holder -> holder.getBonusCapacity(bodyPartId)).orElse(0);
    }

    public static boolean addPersistentCapacity(Player player, ResourceLocation bodyPartId, int amount) {
        return IOrganHolder.resolve(player).map(holder -> {
            holder.addBonusCapacity(bodyPartId, amount);
            return true;
        }).orElse(false);
    }
}
