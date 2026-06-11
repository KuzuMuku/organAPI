package cn.kuzuanpa.organapi.api.query;

import cn.kuzuanpa.organapi.common.capability.IOrganHolder;
import java.util.Collections;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class OrganQueryService {
    private OrganQueryService() {
    }

    public static int getTotalCapacity(Player player, ResourceLocation bodyPartId) {
        return IOrganHolder.get(player).map(holder -> holder.getCapacity(bodyPartId)).orElse(0);
    }

    public static int getUsedCapacity(Player player, ResourceLocation bodyPartId) {
        return IOrganHolder.get(player).map(holder -> holder.getUsedCapacity(bodyPartId)).orElse(0);
    }

    public static int getFreeCapacity(Player player, ResourceLocation bodyPartId) {
        return IOrganHolder.get(player).map(holder -> holder.getFreeCapacity(bodyPartId)).orElse(0);
    }

    public static List<ItemStack> getInstalledOrgans(Player player, ResourceLocation bodyPartId) {
        return IOrganHolder.get(player).map(holder -> holder.getInstalledOrgans(bodyPartId)).orElse(Collections.emptyList());
    }

    public static BodyPartOverview getOverview(Player player, ResourceLocation bodyPartId) {
        return new BodyPartOverview(
                getTotalCapacity(player, bodyPartId),
                getUsedCapacity(player, bodyPartId),
                getInstalledOrgans(player, bodyPartId)
        );
    }
}
